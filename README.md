# Diffy

**(experimental branch - some things do not work yet)**

Neural Network for Clojure

* Dense layer with a single or multiple bias values
* Sigmoid activation function
* 3 Matrix implementations:
    * native Clojure vectors
    * clojure.core.matrix
    * uncomplicate.neanderthal.native

The Neanderthal implementation gets enabled by uncommenting
the corresponding dependencies in `deps.edn` and the :require and
fixture :each entries in `diffy_test.clj`. 

## Tests

    $ clj -Atest

## Diffy Examples

### Mnist

Get mnist_784_csv.csv from https://datahub.io/machine-learning/mnist_784


    $ clj -m examples.mnist          

Uses clojure.core.matrix. Neanderthal implementation can be enabled.

### Cartpole

    $ clj -m examples.cartpole    ;; classic cartpole

