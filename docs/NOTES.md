# NOTES

###### main.py

I have a `parsed_files` array containing `BpmnModel` objects. Each object has:

* Elements:
  * Sequence Flow
  * Task
  * Manual
  * User Task
  * Service Task
  * Event
  * Start Event
  * End Event
  * Gateway
  * Parallel gateway
  * Exclusive Gateway
* Flow
* Instances
* Pending

Currently I'm only using the `Elements` structure. Maybe I could use the `Flow` structure but I don't know how.

1. For each `bpmn_type` there must be some **Natural Language descriptions**, they have to take into account the **previous element** and the **following element**. I created a JSON which describes this information. It has to be read from the `bpmn_types.py` to use the descriptions.
2. For each `element` I have to store the `name`, the `bpmn_type` and a `sintactic_analysis` array. This one must store if each word of the `name` attribute is a verb, an adjective, an adverb, a noun...

This will help me to feed the `simpleNLG` and create text for each BPMN.