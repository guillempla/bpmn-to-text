import os
import pickle
from pathlib import Path
from src.bpmn_parser import BpmnModel

BPMN_PATH = "../bpmn"
PICKLE_PATH = "../parsed_bpmn/"

def equal_models(model1, model2):
    return len(model1.elements) == len(model2.elements) and \
           len(model1.flow) == len(model2.flow) and \
           len(model1.instances) == len(model2.instances) and \
           len(model1.pending) == len(model2.pending)


def compare_arrays(array1, array2):
    true_array = [False for element in range(len(array1))]
    for a in array1:
        for b in array2:
            if equal_models(a, b):
                true_array = True

    return true_array


def save_model_as_picke(file_name, model):
    name_file = PICKLE_PATH + file_name + '.txt'
    with open(name_file, 'wb') as fh:
            pickle.dump(model, fh)


def read_parsed_bpmn():
    pickle_parsed_files = []

    for file in Path(PICKLE_PATH).glob('**/*.txt'):
        file_path = os.fspath(file)
        print(file_path)
        pickle_off = open(file_path, "rb")
        model = pickle.load(pickle_off)
        pickle_parsed_files.append(model)
    return pickle_parsed_files


def read_bpmn_files():
    """
    Finds all BPMN files inside the DATA_PATH, parses it and stores it to an array of BpmnModel.

    :return:
    bpmn_array: Array of BpmnModel objects, each position contains the BPMN parsed into an BpmnModel object.
    """

    bpmn_array = []

    for file in Path(BPMN_PATH).glob('**/*.bpmn'):
        model = BpmnModel(os.fspath(file))
        print(file.name)
        save_model_as_picke(file.name, model)
        bpmn_array.append(model)

    return bpmn_array


def main():
    print(Path.cwd())

    parsed_files = read_bpmn_files()
    pickle_parsed_files = read_parsed_bpmn()
    print(compare_arrays(parsed_files, pickle_parsed_files))

if __name__ == "__main__":
    main()
