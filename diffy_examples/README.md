# Diffy Examples
 
## Run

### Mnist

#### Prerequisites

Get mnist_784_csv.csv from https://datahub.io/machine-learning/mnist_784

#### Run

    $ clj -m mnist.mnist          ;; with clojure.core.vector
    $ clj -m mnist.mnist-neander  ;; with uncomplicate.neanderthal.native

### Cartpole

    $ clj -m cartpole.cartpole    ;; our classic cartpole 
    $ clj -m cartpole.torso       ;; not cartpole, used for development and to be replaced by another example
