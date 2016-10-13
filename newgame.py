from random import choice, randint
import sys

game_no = 2
num_persons = 10

try: 
	game_no = int(sys.argv[1])
	num_persons = int(sys.argv[2])
except: pass

attrs = {
	"hairLength": "white yellow green red blue brown black".split(" "),
	"glasses": "green yellow".split(" "),
	"facialHair": "white yellow blue red".split(" "),
	"eyeColor": "brown blue green".split(" "),
	"pimples": "white yellow green red blue brown black".split(" "),
	"hat": "white yellow green red blue brown black".split(" "),
	"hairColor": "white black brown red blue".split(" "),
	"noseShape": "white yellow green red black".split(" "),
	"faceShape": "white black green red blue brown".split(" ")
}

out = ""
# print attrs
for attr in attrs:
	out += " ".join([attr] + attrs[attr]) + "\n"

for i in range(1, num_persons + 1):
	out += "\nP%d\n" % i
	for attr in attrs:
		out += "%s %s\n" % (attr, choice(attrs[attr]))

with open("game%d.config" % game_no, "w") as outfile:
	outfile.write(out)

with open("game%d.chosen" % game_no, "w") as outfile:
	outfile.write("P%s P%s\n" % (randint(1, num_persons // 2), randint(num_persons // 2, num_persons)))