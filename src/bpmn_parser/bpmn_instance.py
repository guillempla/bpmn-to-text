from .bpmn_model import EndEvent, UserTask, Task, ExclusiveGateway, ParallelGateway
from .user_form_message import UserFormMessage
from copy import deepcopy
from functools import partial
from collections import deque

class BpmnInstance:
    def __init__(self, id, model, variables, in_queue):
        self.id = id
        self.model = model
        self.variables = deepcopy(variables)
        self.in_queue = in_queue
        self.state = "initialized"
        self.pending = deepcopy(self.model.pending)

    def get_info(self):
        return {
            "state": self.state,
            "variables": self.variables,
            "id": self.id,
            "pending": [x.id for x in self.pending],
        }

    @classmethod
    def check_conditions(cls, state, conditions, log):
        log(f"\t- checking variables={state} with {conditions}... ")
        ok = False
        try:
            ok = all(eval(c, deepcopy(state), None) for c in conditions)
        except Exception:
            pass
        log("\t  DONE: Result is", ok)
        return ok

    async def run(self):

        self.state = "running"
        id = self.id
        prefix = f"\t[{id}]"
        log = partial(print, prefix)  # if id == "2" else lambda *x: x

        in_queue = self.in_queue
        self.pending = deepcopy(self.model.pending)
        elements = deepcopy(self.model.elements)
        flow = deepcopy(self.model.flow)
        queue = deque()

        while len(self.pending) > 0:

            # process incoming messages
            if not in_queue.empty():
                queue.append(in_queue.get_nowait())
            # print("Check", id, id(queue), id(in_queue))

            exit = False
            can_continue = False

            message = queue.pop() if len(queue) else None
            if message:
                log("--> msg in:", message and message.taskid)

            for idx, current in enumerate(self.pending):
                if isinstance(current, EndEvent):
                    exit = True
                    break

                if isinstance(current, UserTask):
                    if (
                            message
                            and isinstance(message, UserFormMessage)
                            and message.taskid == current.id
                    ):
                        user_action = message.form_data

                        log("DOING:", current)
                        if user_action:
                            log("\t- user sent:", user_action)
                        can_continue = current.run(self.variables, user_action)
                else:
                    if isinstance(current, Task):
                        log("DOING:", current)
                    can_continue = current.run()

                if can_continue:
                    del self.pending[idx]
                    break

            if exit:
                break

            default = current.default if isinstance(current, ExclusiveGateway) else None

            if can_continue:
                next_tasks = []
                if current.id in flow:
                    default_fallback = None
                    for sequence in flow[current.id]:
                        if sequence.id == default:
                            default_fallback = elements[sequence.target]
                            continue

                        if sequence.conditions:
                            if self.check_conditions(
                                    self.variables, sequence.conditions, log
                            ):
                                next_tasks.append(elements[sequence.target])
                        else:
                            next_tasks.append(elements[sequence.target])

                    if not next_tasks and default_fallback:
                        log("\t- going down default path...")
                        next_tasks.append(default_fallback)

                for next_task in next_tasks:
                    if next_task not in self.pending:
                        self.pending.append(next_task)
                        # log("-----> Adding", next_task)
                    # log("n", next_task)
                    if isinstance(next_task, ParallelGateway):
                        next_task.add_token()
            else:
                log("Waiting for user...", self.pending)
                queue.append(await in_queue.get())

        log("DONE")
        self.state = "finished"
        self.pending = []
        return self.variables
