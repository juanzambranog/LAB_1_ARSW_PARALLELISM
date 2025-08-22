
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch


### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.

## Solucion propuesta

![alt text](/img/image.png)


2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].

	![alt text](/img/image-1.png)

    2. Inicie los tres hilos con 'start()'.

	![alt text](/img/image-2.png)

	3. Ejecute y revise la salida por pantalla. 

	![alt text](/img/image-3.png)

	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.

	![alt text](/img/image-4.png)

	![alt text](/img/image-5.png)

	En orden con run() y en desorden con start()


## **Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.
    
    ![img.png](img/img.png)

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.
    
    ### Agregamos en parametro N y hacemos el manejo de cuando este es par o impar
    ![img_1.png](img/img_1.png)

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.

    ### Realizamos las pruebas con las IP sugeridas (202.24.34.55 y 212.24.24.55) para verificar el correcto funcionamiento de la solucion propuesta.
    ## PRUEBA HOST 202.24.34.55

    ![img_2.png](img/img_2.png)
    ![img_3.png](img/img_3.png)

    ## PRUEBA HOST 212.24.24.55
    
    ![img_4.png](img/img_4.png)
    ![img_5.png](img/img_5.png)

## **Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

        Se podría modificar la implementación para que los hilos se detengan tan pronto se alcance el número mínimo de ocurrencias requerido. 
        Para esto habría que compartir un contador global sincronizado entre los hilos, y que cada hilo verifique antes de continuar si ya se cumplió la condición.
## **Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

### 1. Un solo hilo.
![Pasted Graphic 1.png](img/Pasted%20Graphic%201.png)


### 2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).
![nucleos.png](img/nucleos.png)
![Número de núcleos disponibles 10.png](img/N%C3%BAmero%20de%20n%C3%BAcleos%20disponibles%2010.png)
![Pasted Graphic 2.png](img/Pasted%20Graphic%202.png)
![Pasted Graphic 3.png](img/Pasted%20Graphic%203.png)

### 3. Tantos hilos como el doble de núcleos de procesamiento.
![Pasted Graphic 4.png](img/Pasted%20Graphic%204.png)
![Pasted Graphic 5.png](img/Pasted%20Graphic%205.png)
### 4. 50 hilos.
![Pasted Graphic 6.png](img/Pasted%20Graphic%206.png)
![Pasted Graphic 7.png](img/Pasted%20Graphic%207.png)
### 5. 100 hilos.
![Pasted Graphic 8.png](img/Pasted%20Graphic%208.png)
![Pasted Graphic 9.png](img/Pasted%20Graphic%209.png)
Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):

### HIPOTESIS

#### Modificamos el Main para que nos muestre el tiempo de ejecucion de cada hilo.
![Pasted Graphic 10.png](img/Pasted%20Graphic%2010.png)
![tiempo ejecucion.png](img/tiempo%20ejecucion.png)
#### Usamos Matplotlib en Python para generar el gráfico.
![img.png](img/img.png)
![Tiempo de ejecución vs Número de hilos grafico.png](img/Tiempo%20de%20ejecuci%C3%B3n%20vs%20N%C3%BAmero%20de%20hilos%20grafico.png)

#### La gráfica muestra que el tiempo de ejecución disminuye de manera notable al pasar de 1 a 10 hilos, pero a partir de 20 hilos las mejoras son mínimas. 
#### Esto se explica porque la CPU tiene un número limitado de núcleos y, una vez saturados, los hilos adicionales generan sobrecarga en lugar de acelerar el proceso. Por lo tanto, aumentar hilos más allá de cierto punto no reduce significativamente el tiempo de solución, como se observa entre 50 y 100 hilos, donde los tiempos son prácticamente iguales.

## **Parte IV - Ejercicio Black List Search**

1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 

        El mejor desempeño no se alcanza con 500 hilos porque, de acuerdo con la Ley de Amdahl, siempre hay una parte secuencial que limita la ganancia, 
        y porque al superar el número de núcleos físicos, los hilos generan sobrecarga por cambios de contexto. 
        Por eso, usar 200 o 500 hilos ofrece un rendimiento muy parecido, aunque con menor eficiencia a medida que se aumenta el número de hilos. 

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.

        Cuando se usan tantos hilos como núcleos, se logra el mejor aprovechamiento del hardware. 
        Al usar el doble, apenas hay mejora debido a la sobrecarga de administrar hilos adicionales.

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.

        distribuir el trabajo en varias máquinas ayuda mucho más que meter cientos de hilos en una sola CPU, 
        pero nunca se logra el 100% de eficiencia porque siempre hay tareas secuenciales y coordinación extra.

