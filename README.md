# Diffy

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

#### Prerequisites

Get mnist_784_csv.csv from https://datahub.io/machine-learning/mnist_784

#### Run

    $ clj -m examples.mnist.mnist          ;; with clojure.core.vector
    $ clj -m examples.mnist.mnist-neander  ;; with uncomplicate.neanderthal.native (enable in deps.edn)

### Cartpole

    $ clj -m examples.cartpole.cartpole    ;; classic cartpole

TODO solve