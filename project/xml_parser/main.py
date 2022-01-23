# Python code to parse some BPMN files
import os
import pickle
from pathlib import Path
from bpmn_model import BpmnModel

BPMN_PATH = "../bpmn_models"
PICKLE_PATH = "../parsed_models/"


def save_model_as_pickle(file_name, model):
    name_file = PICKLE_PATH + file_name.replace('.bpmn', '') + '.pickle'
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
        print(file.name)
        model = BpmnModel(file.name, "hola")
        save_model_as_pickle(file.name, model)
        bpmn_array.append(model)

    return bpmn_array


def main():
    # parse all bpmn files
    models_parsed = read_bpmn_files()
    models = read_parsed_bpmn()
    print(models)


if __name__ == "__main__":
    # calling main function
    main()
