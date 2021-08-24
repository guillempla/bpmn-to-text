class UserFormMessage:
    def __init__(self, taskid, form_data=None):
        if form_data is None:
            form_data = {}
        self.taskid = taskid
        self.form_data = form_data
