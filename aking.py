import cv2
import numpy
import sys
from heapq import *
from random import *

from StringIO import StringIO




def heuristic(a, b):
    return (b[0] - a[0]) ** 2 + (b[1] - a[1]) ** 2
    #return abs(b[1] - a[1]) + abs(b[0] - a[0])

def astar(array, start, goal):

    ####################################################################################
    # Create a black image
    img = numpy.zeros((WIDTH,HEIGHT,3), numpy.uint8)
    # DISEGNO GRIGLIA
    W = WIDTH / array.shape[1]
    H = HEIGHT / array.shape[0]
    for y in range(0, array.shape[0]):    
        for x in range(0,array.shape[1]):
            if array[y, x] == 0 :
                cv2.rectangle(img,(x*W,y*H),((x*W)+W-1,(y*H)+H-1),(255,255,255),-1)
            cv2.rectangle(img,(x*W,y*H),((x*W)+W-1,(y*H)+H-1),(190,190,190),1)

    cv2.imshow('result', img)
    ####################################################################################

    # Rendo la destinazione sempre valida
    array[goal[1]][goal[0]] = 0

    neighbors = [(0,1),(0,-1),(1,0),(-1,0)]

    close_set = set()
    came_from = {}
    gscore = {start:0}
    fscore = {start:heuristic(start, goal)}
    oheap = []

    heappush(oheap, (fscore[start], start))
    
    while oheap:

        current = heappop(oheap)[1]

        if current == goal:
            data = []
            while current in came_from:
                data.append(current)
                current = came_from[current]
            ####################################################################################
            # Disegno percorso
            for el in data:
                cv2.rectangle(img,(el[1]*W,el[0]*H),((el[1]*W)+W-1,(el[0]*H)+H-1),(255,0,0),-1)
                cv2.imshow('result', img)
            ####################################################################################
            return data

        close_set.add(current)
        for i, j in neighbors:
            neighbor = current[0] + i, current[1] + j            
            tentative_g_score = gscore[current] + heuristic(current, neighbor)
            if 0 <= neighbor[0] < array.shape[0]:
                if 0 <= neighbor[1] < array.shape[1]:                
                    if array[neighbor[0]][neighbor[1]] == 1:
                        continue
                else:
                    # array bound y walls
                    continue
            else:
                # array bound x walls
                continue
                
            if neighbor in close_set and tentative_g_score >= gscore.get(neighbor, 0):
                continue
                
            if  tentative_g_score < gscore.get(neighbor, 0) or neighbor not in [i[1]for i in oheap]:
                came_from[neighbor] = current
                gscore[neighbor] = tentative_g_score
                fscore[neighbor] = tentative_g_score + heuristic(neighbor, goal)
                heappush(oheap, (fscore[neighbor], neighbor))
                
        ####################################################################################
        for el in oheap : #open green
            cv2.rectangle(img,(el[1][1]*W,el[1][0]*H),((el[1][1]*W)+W-1,(el[1][0]*H)+H-1),(0,255,0),-1)

        for el in close_set : #closed red
            cv2.rectangle(img,(el[1]*W,el[0]*H),((el[1]*W)+W-1,(el[0]*H)+H-1),(0,0,255),-1)
           
            
        cv2.imshow('result', img)
        if cv2.waitKey(20) & 0xFF == ord('q'):
            break  
        
    return "Nessun percorso trovato"

    
# DIMENSIONI DELLA FINESTRA
WIDTH = 700
HEIGHT = 700


a = numpy.loadtxt(StringIO(sys.argv[1]), dtype=numpy.int)

#print astar(RandMapGenerator(100, 100, 0.4), (0,0), (99,99))
print astar(a, (0,0), (8,8))
cv2.waitKey()
cv2.destroyAllWindows()