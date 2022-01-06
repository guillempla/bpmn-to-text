import xml.etree.ElementTree as ET
from .bpmn_types import *
from .bpmn_instance import BpmnInstance
from collections import defaultdict
import asyncio
import json

DESCRIPTIONS_PATH = "../resources/descriptions.json"


class BpmnModel:
    def __init__(self, model_path):
        self.pending = []
        self.elements = {}
        self.flow = defaultdict(list)
        self.instances = {}

        json_file = open(DESCRIPTIONS_PATH)
        self.description = json.load(json_file)

        model_tree = ET.parse(model_path)
        model_root = model_tree.getroot()
        process = model_root.find("bpmn:process", NS)

        for tag, _type in BPMN_MAPPINGS.items():
            for e in process.findall(f"{tag}", NS):
                t = _type()
                t.parse(e)

                if isinstance(t, SequenceFlow):
                    self.flow[t.source].append(t)

                if isinstance(t, ExclusiveGateway):
                    if t.default:
                        self.elements[t.default].default = True

                self.elements[t.id] = t

                if isinstance(t, StartEvent):
                    self.pending.append(t)

    async def create_instance(self, id, variables):
        queue = asyncio.Queue()
        instance = BpmnInstance(id, model=self, variables=variables, in_queue=queue)
        self.instances[id] = instance
        return instance
