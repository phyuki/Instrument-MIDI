# Instrument-MIDI
Aplicação mobile para a recepção de mensagens do protocolo OSC (Open Sound Control) e o tratamento das mesmas para a tradução no protocolo MIDI, responsável por fazer o celular tocar a música correspondente.

O aplicativo recebe mensagens com o seguinte formato:
"/message/piano/{1,C4,F#4,D5}"

O campo correspondente ao "piano" representa o timbre do instrumento. Até então, o projeto suporta "drums", "voice", "piano" ou "guitar".

O campo 1 representa o estado de NOTE_ON, enquanto 0 representa NOTE_OFF, instruções referentes ao início e término do som da nota musical inserida.

As notas musicais seguem a notação ocidental inglesa com escala cromática.
Caso seja um acorde a ser reproduzido, apenas inserir o conjunto de notas do acorde, separado por vírgula como feito no exemplo anterior.
Ex.: C4, F#4, D5, A3...

Utiliza-se das bibliotecas JavaOSC e mididriver, disponíveis no próprio Github, pelos autores hoijui e billtherfarmer.
