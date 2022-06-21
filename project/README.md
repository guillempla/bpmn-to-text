# Instruccions

1. Primer cal còrrer el `bpmn_parser`. Això genera uns arxius `.json`amb la informació dels **BPMN** parsejada a la carpeta `parsed_bpmn/`
2. Segon cal executar el `freeling_client`. Aquest programa rep els **BPMN** parsejats anteriorment i hi afegeix l'anàlisi morfosintàctic dels noms de cada element del model. Això ho guarda en un `.json` a la carpeta `freeling_responses/`.
3. Després cal generar les frases simples. Això ho fem executant el `sentences_generator`. Aquest programa rep la informació recopilada anteriorment i la tracta per a crear una frase.
4. Finalment, executem el programa `sentences_joiner`. Aquest programa rep les frases simples dels models i els uneix en un sol paràgraf.