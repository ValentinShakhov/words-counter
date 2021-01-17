# wordsCounter

How it works:

File readers scan through the files word by word, putting each word into the queue for processing. There is a queue listener, which maintains a map where each entry is a tuple of the word and its corresponding internal representaion node. Such a node contains the word itself, its count per file and references to the nodes with a larger and smaller total count. There is a node sorted list which holds the reference to the node with the least and largest count.

After readers and the listener have finished their work, the chain of nodes is printed out in a loop.
