# This project is to show how to create a high concurrency. high availiablity, high performance framwork. It can handle millions like requests per second.
# This focuses on the tech part not business, so will not handle login/register user module etc.
# I try to answer a tech using the following logic:
    ## what is it? (principle)
    ## what is for? (use case)
    ## why choose it?(other techs)
    ## how to use it?(code)

# Frontend: use reactjs + vite + tailwindcss
## frontend only shows the reactjs framwork + redux
# Backend: use springboot3 + java21 + redis + mysql 
## cache: Redis(Lua script), Caffeine(HeavyKeeper)
## message queue: Pulsar
## visiualisation:  Prometheus + Grafana

# original version is frontend(vue) + backend(mybatis-plus)


