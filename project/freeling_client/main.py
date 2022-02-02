# Freeling client. Read sentences from a file, sends them to Freeling, and saves to disk the
# syntactic analysis.

from pathlib import Path
import sys
import json
import requests

# Variables to connect with Freeling's Docker
HOST = "172.17.0.2"
LANGID = "50005"
FREELING = "60006"

# Language identification and text analysis service URL
langid = "http://" + HOST + ":" + LANGID
freeling = "http://" + HOST + ":" + FREELING

PARSED_PATH = "../parsed_bpmn/"
FREELING_PATH = "../freeling_responses/"


def send_parsed_texts(file_path):
    lines = read_file_lines(file_path)

    for line in lines:
        # Clean text from newlines and trailing spaces.
        line = line.strip()

        # Find out text language
        resp = call_server(langid, {'text': line})
        lang = json.loads(resp)["language"]

        # Get predicate task label analysis from freeling
        resp = call_server(freeling, {'text': line,
                                      'language': lang,
                                      'OutputLevel': 'shallow'})

        # Save to disk
        save_to_disk(FREELING_PATH + file_path.name, resp)


def read_file_lines(file_path):
    file = open(file_path, 'r')
    lines = file.readlines()
    file.close()
    return lines


def call_server(url, request):
    # Set query elements
    request_headers = {'Content-Type': 'application/json; charset=utf-8'}
    request_data = json.dumps(request, ensure_ascii=False).encode('utf-8')

    # Send request and get response
    resp = requests.post(url, data=request_data, headers=request_headers)
    # HTTP error, raise exception
    if resp.status_code != requests.codes.ok:
        print("ERROR", resp.status_code, "- Reason:", resp.headers["Reason"])
        resp.raise_for_status()

    # Extract language from answer
    return resp.text


def save_to_disk(name, response):
    out_file = open(name + ".json", "w")
    json.dump(response, out_file)
    out_file.close()


def main():
    global HOST
    if len(sys.argv) > 1:
        HOST = sys.argv[1]

    for file_path in Path(PARSED_PATH).glob('**/*.txt'):
        send_parsed_texts(file_path)


if __name__ == '__main__':
    main()
