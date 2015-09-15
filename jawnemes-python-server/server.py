import numpy as np
from matplotlib.mlab import PCA
import mlpy
import matplotlib.pyplot as plt
import threading
import time
import serial

import logging
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

from flask import Flask, url_for
from flask.ext.cors import CORS
app = Flask(__name__)
cors = CORS(app)

start = False
finish = False
resultReady = False
result = ''

@app.route('/')
def api_root():
    global start
    global finish
    global resultReady
    global result

    if not start:
        start = True
        return 'Started!'
    else:
        finish = True
        while not resultReady: time.sleep(0.01)
        return result

def runSite():
    print 'Running Flask thing\n'
    app.run(host='0.0.0.0')

if __name__ == '__main__':
    t = threading.Thread(target=runSite)
    t.start()

    prevPCA = None

    while True:
        rawdata = []

        ser = serial.Serial('/dev/cu.usbmodem485911', 115200, timeout=1)
        while not start: time.sleep(0.01)
        print 'STARTING!'
        while not finish:
            rawdata.append(ser.readline())
        ser.close()
        print 'FINISHED!'


        data = [[], [], []]
        gyro = [[], [], []]
        #data will be a 2d array
        #each column is a sample
        #prox1, prox2, prox4, gyroX, gyroY, gyroZ

        for line in rawdata:
            line = line.strip()
            vals = line.split(' ')

            if len(vals) != 11:
                print 'UNRECOGNIZED LINE IN DATA'
                print vals
                continue

            data[0].append(float(vals[3])) #prox1
            data[1].append(float(vals[4])) #prox2
            data[2].append(float(vals[6])) #prox4
            gyro[0].append(float(vals[7])) #gyroX
            gyro[1].append(float(vals[8])) #gyroY
            gyro[2].append(float(vals[9])) #gyroZ

        data = np.array(data)

        minvals = []
        ranges = []
        for i in range(len(data)):
            #smoothing window of 5 (based of matlab's smooth function)
            data[i][1] = np.average(data[i][0:3])
            for x in range(len(data[i]))[2:-2]:
                data[i][x] = np.average(data[i][x-2:x+3])
            data[i][-2] = np.average(data[i][-3:])

            #normalizing
            minvals.append(np.min(data[i]))
            ranges.append(np.max(data[i]) - minvals[-1])

        index = np.argmax(ranges)
        for i in range(len(data)):
            data[i] = (data[i] - minvals[index])/ranges[index]

        # plt.figure(1)
        # plt.plot(data[0], label='Prox1')
        # plt.plot(data[1], label='Prox2')
        # plt.plot(data[2], label='Prox4')
        # plt.legend()

        #transpose for PCA, then transpose back
        pcaData = PCA(data.T).Y.T

        # plt.figure(2)
        # plt.plot(pcaData[0], label='PCA0')
        # plt.plot(pcaData[1], label='PCA1')
        # plt.plot(pcaData[2], label='PCA2')
        # plt.legend()

        dist = 0
        if prevPCA is not None:
            dist = mlpy.dtw_std(pcaData[0], prevPCA, dist_only=True)
            print dist

        prevPCA = pcaData[0]

        result = 'Dist: ' + str(dist)
        start = False
        finish = False
        resultReady = True

        #plt.show()
