# Red Hog

Because there's nothing better than overengineering and overcomplicating stuff, this is a REST API designed to stress your server. It hogs up CPU processing power by generating prime numbers for a given length of time.

## Endpoints

- `/hog?timeout=10&threads=10`
	- This will spawn 10 workers which will generate prime numbers for 10 seconds.
	- If you look at the stdout, each worker has a name and a designated 'pen' (you know, hogs live in pens), and they announce the latest prime number they find.
	- Defaults to 1 hog working for 10 seconds


- `/hog/abort`
	- It kills all of the running hogs ( D: )


- `/simulate?duration=500&timeunit=milliseconds`
	- Does a 'realistic' call and gives out a 'realistic' response. In this case, it takes 500 milliseconds and then replies with a JSON
	- It accepts milliseconds, seconds and minutes
	- Setting the duration to -1, makes it a random number between 0 and 1000
	- timeunit defaults to milliseconds

## Prime saving

This has an accompanying `docker-compose.yml` file that defines a Redis server. This is because if the hogs can connect to it, they will save the highest prime they find (in a hashset, like: pen/pig/prime).

If you unset the enviroment variable, they won't connect to anything and won't save their precious prime numbers.

This can also be used to stress out the network, of course.

## TODO
- Make it so that the response from `/hog` shows the numbers generated
- Stats endpoint that show all generated primes from all pens
