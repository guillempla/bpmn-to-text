class Process:

    def __init__(self, id):
        self.id = id
        self.events = []
        self.flows = []
        self.gateways = []

    def getId(self):
        return self.id

    def getEvents(self):
        return self.events

    def getFlows(self):
        return self.flows

    def getGateways(self):
        return self.gateways

    def getElements(self):
        return self.events + self.flows + self.gateways

    def setId(self, id):
        self.id = id

    def setEvents(self, events):
        self.events = events

    def setFlows(self, flows):
        self.flows = flows

    def setGateways(self, gateways):
        self.gateways = gateways
