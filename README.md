# Red Hog

Because there's nothing better than overengineering and overcomplicating stuff, this is a REST API designed to stress your server. It hogs up CPU processing power by generating prime numbers for a given length of time.

## Endpoints

- `/hog?timeout=10&threads=10`
	- This will spawn 10 workers which will generate prime numbers for 10 seconds.
	- If you look at the stdout, each worker has a name and a designated 'pen' (you know, hogs live in pens), and they announce the latest prime number they find.
	- Defaults to 1 hog working for 10 seconds
	- It returns the generated pen's number


- `/hog/abort`
	- It kills all of the running hogs ( D: )


- `/simulate?duration=500&timeunit=milliseconds&failChance=0.5`
	- Does a 'realistic' call and gives out a 'realistic' response. In this case, it takes 500 milliseconds and then replies with a JSON, and there's a 50% chance that it will fail
	- It accepts milliseconds, seconds and minutes
	- Setting the duration to -1, makes it a random number between 0 and 1000
	- timeunit defaults to milliseconds
	- failChance is a float between 0.0 (which is the default) and 1.0


- `/stats?pen=12345678`
	- Shows each pig pen with each of the pigs' names and the highest prime they found.
	- Using the 'pen' parameter, shows the pigs and numbers from that pen; if the parameter is omitted, it shows all pens.


## Prime saving

This has an accompanying `docker-compose.yml` file that defines a Redis server. This is because if the hogs can connect to it, they will save the highest prime they find (in a hashset, like: pen/pig/prime).

If you unset the enviroment variable, they won't connect to anything and won't save their precious prime numbers.

This can also be used to stress out the network, of course.

## Building & Running
The Java project is built using Maven, which packages it into a Docker image.

- `cd` into `redhog/` and run `mvn clean package docker:build`
- Once it's done, `cd` to where the `docker-compose.yml` file is located, and run `docker-compose up`
- If you don't want the Redis server, just comment it out (and the 'environment' variable in the 'web' component of the yml).

### Troubleshooting
If the `docker:build` phase fails, it's very likely it's because you have uncommited changes. The Docker Maven plugin requires you to have no uncommited changes. Either commit them, or just delete the `.git/` folder (WARNING: Of course keep in mind that this will delete any branches and you will be basically removing all Git from your project. If you made any changes, DON'T do this.)

## TODO
- Distributed hogging: Installable agents that stress the CPU by the orders of a command center.
