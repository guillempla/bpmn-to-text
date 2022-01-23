class Process:

    def __init__(self, process_id):
        self.id = process_id
        self.events = []
        self.flows = []
        self.gateways = []
        self.lanes_set = []

    def get_id(self):
        return self.id

    def get_events(self):
        return self.events

    def get_flows(self):
        return self.flows

    def get_gateways(self):
        return self.gateways

    def get_elements(self):
        return self.events + self.flows + self.gateways

    def get_lanes_set(self):
        return self.lanes_set

    def set_id(self, process_id):
        self.id = process_id

    def set_events(self, events):
        self.events = events

    def set_flows(self, flows):
        self.flows = flows

    def set_gateways(self, gateways):
        self.gateways = gateways

    def set_lanes_set(self, lanes_set):
        self.lanes_set = lanes_set
