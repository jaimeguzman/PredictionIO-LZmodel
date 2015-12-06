
## To Run this Experiment yoi mus to first of all


run 

pio-start-all
pio eventserver


check if everything is working


pio status

Create the apps for store the data

```
    pio app new exp_tokens_lt_5_10 &
    pio app new exp_tokens_lt_5_100 &
    pio app new exp_tokens_lt_5_1000 &
    pio app new exp_tokens_lt_5_10000 &
    pio app new exp_tokens_lt_5_100000 &
    pio app new exp_okens_lt_5_1000000 &
```


later you have to import all the event (Data)


