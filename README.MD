# Artifiical Intelligence local-and-stochastic-research-methods

## Este trabalho foi proposto na cadeira de Inteligência Artificial CC2006 com objetivo de estudar métodos de pesquisa local e pesquisa local estocástica usando um problema de geração de polígonos simples. Este problema tem como base o gerador de polígonos aleatórios (RPG) de Thomas Auer e Martin Held, que consiste na geração aleatória de polígonos simples a partir de um conjunto também aleatorio de pontos usando diferentes heuristicas. De referir que polígonos simples não tem arestas que se intersectam a não ser em vértices e importante de elucidar que as arestas dos polígonos devem formar um ciclo de Hamilton, isto é, um ciclo de Hamilton é um percurso fechado que não repete nós e passa em todos os nós. Assim sendo soluções otimas são apenas poligonos que sejam simples. Acima de tudo, neste trabalho para a implementação dos diferentes algoritmos que nos eram pedidos usamos a linguagem Java e a ferramenta Geogebra para os gráficos dos polígonos aqui referidos.

### How to use

- Compile and run
    - javac Ai.java & java Ai

### Important Instructions

- O programa pergunta se são aleatorios os pontos, número de pontos, tamanho do mapa caso seje random e o valor de kmax para testes no simulated annealing
- Para testes, deve ser respondido escolhida a opçao não random, e responder as 3 primeiras perguntas e depois dar paste das coordenadas na ficheiros, caso contrario o programa empanca no input
- Apenas existe grafico para o caso de 50 pontos, usando o geogebra online
