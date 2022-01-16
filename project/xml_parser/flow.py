class Flow():

    def __init__(self, id, incoming, outgoing):
        self.id = id
        self.incoming = incoming
        self.outgoing = outgoing

    def getId(self):
        return self.id

    def setId(self, id):
        self.id = id

    def getIncoming(self):
        return self.incoming

    def getOutgoing(self):
        return self.outgoing

    def setIncoming(self, incoming):
        self.incoming = incoming

    def setOutgoing(self, outgoing):
        self.outgoing = outgoing
