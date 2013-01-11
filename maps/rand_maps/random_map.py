#!/usr/bin/python

import sys
import random

def randSquare(mineProb, encampProb):
    r = random.randint(0,99)
    if r < mineProb:
        return 'o'
    elif r < mineProb + encampProb:
        return '#'
    else:
        return '.'

def genRandom180SymMap(height, width, mineProb, encampProb):
    if mineProb+encampProb >= 100:
        print "mineProb + encampProb > 100\%, aborting!"
        return ''

    p1, p2 = ('a', 'A') if random.random() > 0.5 else ('A', 'a')
    vertical = True if random.random() > 0.5 else False

    l = height*width

    s = [randSquare(mineProb, encampProb) for i in range(l/2)]

    baseLoc = random.randint(1, l/3)

    s1 = s[:baseLoc-1] + ['.', p1, '.'] + s[baseLoc+2:]
    if vertical:
        if baseLoc >= height:
            s1[baseLoc - height] = '.'
        if baseLoc + height < len(s1):
            s1[baseLoc+height]  = '.'
    else:
        if baseLoc >= width:
            s1[baseLoc - width] = '.'
        if baseLoc + width < len(s1):
            s1[baseLoc+width]  = '.'

    s2 = list(s1)
    s2[baseLoc] = p2

    s2.reverse()

    onelinemap = s1 + [randSquare(mineProb, encampProb)] + s2 if l%2 == 1 else s1+s2

    if vertical:
        themap = [ [] for j in range(height) ]
        for i in range(l):
            themap[i%height].append(onelinemap[i])
    else:
        themap = []
        c = 0
        for i in range(height):
            line = []
            for j in range(width):
                line.append(onelinemap[c])
                c += 1
            themap.append(line)

    s = '\n'.join(''.join(t) for t in themap)

    return """<?xml version="1.0" encoding="UTF-8"?>
<map height="%d" width="%d">
    <game seed="%d" rounds="2000"/>
    <symbols>
        <symbol terrain="LAND" type="TERRAIN" character="."/>
        <symbol team="NEUTRAL" type="ENCAMPMENT" character="#"/>
        <symbol team="A" type="HQ" character="A"/>
        <symbol team="B" type="HQ" character="a"/>
        <symbol team="NEUTRAL" type="MINE" character="o"/>
    </symbols>
    <data>
<![CDATA[
%s
]]>
    </data>
</map>
""" %\
(height, width, random.randint(0,65535), s)

if __name__ == '__main__': 
    try:
        minheight = int(sys.argv[1])
        maxheight = int(sys.argv[2])
        minwidth = int(sys.argv[3])
        maxwidth = int(sys.argv[4])
        
        minmineProb = int(sys.argv[5])
        maxmineProb = int(sys.argv[6])
        minencampProb = int(sys.argv[7])
        maxencampProb = int(sys.argv[8])

        numMaps = int(sys.argv[9])

    except:
        print "Usage: random_map.py minheight maxheight minwidth maxwidth minMineProb maxMineProb minEncampProb maxEncampProb numMaps \n mineProb, encampProb are percentages whose sum < 100."

    random.seed()

    for i in range(numMaps):
        height = random.randint(minheight, maxheight)
        width = random.randint(minwidth, maxwidth)
        mineProb = random.randint(minmineProb, maxmineProb)
        encampProb = random.randint(minencampProb, maxencampProb)

        fname = 'rand_map_%d_%d_%d_%d_%d.xml' %(i, height, width, mineProb, encampProb)
    
        s = genRandom180SymMap(height, width, mineProb, encampProb)

        with open(fname, 'w') as fout:
            fout.write(s)

