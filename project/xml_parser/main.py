# Python code to parse a BPMN file
import xml.etree.ElementTree as ET
from event import Event
from flow import Flow


def parseXML(xmlfile):
	# create element tree object
	tree = ET.parse(xmlfile)

	# get root element
	root = tree.getroot()
	print(root.tag)
	print()
	# create empty list for storing BPMN elements
	bpmn_events = []
	bpmn_flows = []

	# iterate BPMN items
	for item in root:
		if item.tag.__contains__('process'):
			print("    ", item.tag)

			# empty events and flows dictionary
			events = []
			flows = []

			# iterate child elements of item
			for child in item:
				if (child.tag.__contains__('sequenceFlow')):
					f = Flow(child.attrib['id'], child.attrib['sourceRef'], child.attrib['targetRef'])
					print("         Sequence Flow: ", f.getId())
					print("         Incoming:      ", f.getIncoming())
					print("         Outcoming:     ", f.getOutgoing())
					flows.append(f)
				else:
					e = Event(child.attrib['id'], child.attrib['name'])
					print("         Name:           ", e.getId())
					print("         Id:             ", e.getName())
					events.append(e)

				print()

			# append news dictionary to news items list
			bpmn_events.append(events)
			bpmn_flows.append(flows)

			print()

	# return BPMN elements list
	return bpmn_events + bpmn_flows


def main():
	# model_path of BPMN model
	model_path = '../bpmn_models/A10/A.1.0.bpmn'

	# parse xml file
	model_parsed = parseXML(model_path)

	print(model_parsed)


if __name__ == "__main__":
	# calling main function
	main()
