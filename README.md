# ArenaDesign
### What is ArenaDesign?

ArenaDesign is a JavaFX program that I made mid-summer that displays a grid that users can interact with 
(similar to a system like Paint) to design their own laser tag arena.

### What I learned

This project was a great way to learn JavaFX, which is (through cursory research) one of the more modern ways to make GUI applications
in Java. Once I got past the initial learning curve, it was mostly just a matter of me looking up methods I needed and quite a bit of 
troubleshooting.

### Challenges

Due to the way I designed the program, it essentially automatically resizes to fit the content that is on the grid. This would
normally be good, but made it so I had to express most stuff in terms of screen width and other related things, instead of 
hardcoding values (which is a bad practice in general, and I'm glad that wasn't a viable outcome). 

I havent' done it quite yet, but I expect "collision" (in context to this program, locking onto the nearest intersections both at the 
beginning and end of the line) to be pretty difficult. I could hardcode the values, but because you can use whatever values for number 
of rows and columns, I have to account for that (mostly just some math). That will probably take a while to figure out, but will be easy
from then on.

### Thoughts on Java/JavaFX

This is really the first GUI system I've used (except if you count Unity, which I haven't really gotten too in-depth with). However, I 
do like it. Everything was pretty straightforward, and everything fell into place pretty logically, and I didn't have to make too many
logical leaps to fix my issues. Overall, I'm a fan, but even though there are CSS options to fix this, it seems largely outdated. I plan
to learn Python's GUI system (Tkinter gets thrown around a lot) in the future, because it seems cool.


