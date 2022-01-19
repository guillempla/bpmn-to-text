# Python code to parse a BPMN file
import xml.etree.ElementTree as ET
from Elements import Event, Flow, Gateway, Process


def parseXML(xmlfile):
	# create element tree object
	tree = ET.parse(xmlfile)

	# get root element
	root = tree.getroot()
	print(root.tag)
	print()

	processes = []

	# iterate BPMN items
	for item in root:
		if item.tag.__contains__('process'):

			p = Process(item.attrib['id'])
			print("     Process:    ", p.getId())

			# empty events, flows dictionary
			events = []
			gateways = []
			flows = []

			# iterate child elements of item
			for child in item:
				if child.tag.__contains__('sequenceFlow'):
					f = Flow(child.attrib['id'], child.attrib['sourceRef'], child.attrib['targetRef'])
					print("         Sequence Flow: ", f.getId())
					print("         Incoming:      ", f.getIncoming())
					print("         Outcoming:     ", f.getOutgoing())
					flows.append(f)
				elif child.tag.__contains__('exclusiveGateway'):
					g = Gateway(child.attrib['id'], child.attrib['name'], child.attrib['gatewayDirection'])
					print("         Gateway:       ", g.getId())
					print("         Name:          ", g.getName())
					print("         Direction:     ", g.getGatewayDirection())
					gateways.append(g)
				else:
					e = Event(child.attrib['id'], child.attrib['name'])
					print("         Event:         ", e.getId())
					print("         Name:          ", e.getName())
					events.append(e)

				print()

			p.setEvents(events)
			p.setGateways(gateways)
			p.setFlows(flows)
			print()

		processes.append(p)

	# return BPMN elements list
	return processes


def main():
	# model_path of BPMN model
	model_path = '../bpmn_models/A20/A.2.0.bpmn'

	# parse xml file
	model_parsed = parseXML(model_path)

	print(model_parsed)


if __name__ == "__main__":
	# calling main function
	main()
