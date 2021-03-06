/**
 *  Xiaomi Aqara Temperature Humidity Sensor
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  2017-03 First release of the Xiaomi Temp/Humidity Device Handler
 *  2017-03 Includes battery level (hope it works, I've only had access to a device for a limited period, time will tell!)
 *  2017-03 Last checkin activity to help monitor health of device and multiattribute tile
 *  2017-03 Changed temperature to update on .1° changes - much more useful
 *  2017-03-08 Changed the way the battery level is being measured. Very different to other Xiaomi sensors.
 *  2017-03-23 Added Fahrenheit support
 *  2017-03-25 Minor update to display unknown battery as "--", added fahrenheit colours to main and device tiles
 *  2017-03-29 Temperature offset preference added to handler
 *
 *  known issue: these devices do not seem to respond to refresh requests left in place in case things change
 *  known issue: tile formatting on ios and android devices vary a little due to smartthings app - again, nothing I can do about this
 *  known issue: there's nothing I can do about the pairing process with smartthings. it is indeed non standard, please refer to community forum for details
 *
 *  Change log:
 *  bspranger - renamed to bspranger to remove confusion of a4refillpad
 */

metadata {
    definition (name: "Xiaomi Aqara Temperature Humidity Sensor", namespace: "bspranger", author: "bspranger") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Battery"
        capability "Refresh"
        capability "Health Check"

        attribute "lastCheckin", "String"
        attribute "batteryRuntime", "String"

        fingerprint profileId: "0104", deviceId: "5F01", inClusters: "0000, 0003, FFFF, 0402, 0403, 0405", outClusters: "0000, 0004, FFFF", manufacturer: "LUMI", model: "lumi.weather", deviceJoinName: "Xiaomi Aqara Temp Sensor"

        command "resetBatteryRuntime"
    }

    // simulator metadata
    simulator {
        for (int i = 0; i <= 100; i += 10) {
            status "${i}F": "temperature: $i F"
        }

        for (int i = 0; i <= 100; i += 10) {
            status "${i}%": "humidity: ${i}%"
        }
    }

    preferences {
        section {
            input title: "Temperature Offset", description: "This feature allows you to correct any temperature variations by selecting an offset. Ex: If your sensor consistently reports a temp that's 5 degrees too warm, you'd enter '-5'. If 3 degrees too cold, enter '+3'. Please note, any changes will take effect only on the NEXT temperature change.", displayDuringSetup: true, type: "paragraph", element: "paragraph"
            input "tempOffset", "number", title: "Degrees", description: "Adjust temperature by this many degrees", range: "*..*", displayDuringSetup: true, defaultValue: 0, required: true
        }
        section {
            input name: "PressureUnits", type: "enum", title: "Pressure Units", options: ["mbar", "kPa", "inHg", "mmHg"], description: "Sets the unit in which pressure will be reported", defaultValue: "mbar", displayDuringSetup: true, required: true
        }
        section {
            input title: "Pressure Offset", description: "This feature allows you to correct any pressure variations by selecting an offset. Ex: If your sensor consistently reports a pressure that's 5 too high, you'd enter '-5'. If 3 too low, enter '+3'. Please note, any changes will take effect only on the NEXT pressure change.", displayDuringSetup: true, type: "paragraph", element: "paragraph"
            input "pressOffset", "number", title: "Pressure", description: "Adjust pressure by this many units", range: "*..*", displayDuringSetup: true, defaultValue: 0, required: true
        }
    }

    // UI tile definitions
    tiles(scale: 2) {
        multiAttributeTile(name:"temperature", type:"generic", width:6, height:4) {
            tileAttribute("device.temperature", key:"PRIMARY_CONTROL") {
                attributeState("temperature", label:'${currentValue}°',
                    backgroundColors:[
                        [value: 0, color: "#153591"],
                        [value: 5, color: "#1e9cbb"],
                        [value: 10, color: "#90d2a7"],
                        [value: 15, color: "#44b621"],
                        [value: 20, color: "#f1d801"],
                        [value: 25, color: "#d04e00"],
                        [value: 30, color: "#bc2323"],
                        [value: 44, color: "#1e9cbb"],
                        [value: 59, color: "#90d2a7"],
                        [value: 74, color: "#44b621"],
                        [value: 84, color: "#f1d801"],
                        [value: 95, color: "#d04e00"],
                        [value: 96, color: "#bc2323"]
                    ]
                )
            }
        }
        standardTile("humidity", "device.humidity", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue}%', icon:"st.Weather.weather12"
        }
        standardTile("pressure", "device.pressure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue}', icon:"st.Weather.weather1"
        }
        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
            state "default", label:'${currentValue}%', unit:"",
            backgroundColors:[
                [value: 0, color: "#c0392b"],
                [value: 25, color: "#f1c40f"],
                [value: 50, color: "#e67e22"],
                [value: 75, color: "#27ae60"]
            ]
        }
        valueTile("temperature2", "device.temperature", decoration: "flat", inactiveLabel: false) {
            state "temperature", label:'${currentValue}°', icon: "st.Weather.weather2",
                backgroundColors:[
                    [value: 0, color: "#153591"],
                    [value: 5, color: "#1e9cbb"],
                    [value: 10, color: "#90d2a7"],
                    [value: 15, color: "#44b621"],
                    [value: 20, color: "#f1d801"],
                    [value: 25, color: "#d04e00"],
                    [value: 30, color: "#bc2323"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
        }
        valueTile("lastcheckin", "device.lastCheckin", decoration: "flat", inactiveLabel: false, width: 4, height: 1) {
            state "default", label:'Last Checkin:\n ${currentValue}'
        }
        valueTile("batteryRuntime", "device.batteryRuntime", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
            state "batteryRuntime", label:'Battery Changed (tap to reset):\n ${currentValue}', unit:"", action:"resetBatteryRuntime"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["temperature2"])
        details(["temperature", "battery", "humidity", "pressure", "lastcheckin", "batteryRuntime", "refresh"])
    }
}

def installed() {
// Device wakes up every 1 hour, this interval allows us to miss one wakeup notification before marking offline
    log.debug "Configured health checkInterval when installed()"
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def updated() {
// Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline
    log.debug "Configured health checkInterval when updated()"
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

// Parse incoming device messages to generate events
def parse(String description) {
    log.debug "${device.displayName}: Parsing description: ${description}"
    // send event for heartbeat
    def now = new Date().format("EEE MMM dd yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
    sendEvent(name: "lastCheckinDate", value: nowDate)

    Map map = [:]

    if (description?.startsWith("temperature: ")) {
        map = parseTemperature(description)
    } else if (description?.startsWith("humidity: ")) {
        map = parseHumidity(description)
    } else if (description?.startsWith('catchall:')) {
        map = parseCatchAllMessage(description)
    } else if (description?.startsWith('read attr - raw:')) {
        map = parseReadAttr(description)
    }
    def results = null
    if (map)
    {
    	log.debug "${device.displayName}: Parse returned ${map}"
    	results =createEvent(map)
    }
    else
    {
    	log.debug "${device.displayName}: was unable to parse ${description}"
    }
    return results
}


private Map parseTemperature(String description){
    def temp = ((description - "temperature: ").trim()) as Float
    if (tempOffset == null || tempOffset == "" ) tempOffset = 0
    if (temp > 100) {
      temp = 100.0 - temp
    }
    if (getTemperatureScale() == "C") {
        if (tempOffset) {
            temp = (Math.round(temp * 10))/ 10 + tempOffset as Float
        } else {
            temp = (Math.round(temp * 10))/ 10 as Float
        }
    } else {
        if (tempOffset) {
            temp =  (Math.round((temp * 90.0)/5.0))/10.0 + 32.0 + tempOffset as Float
        } else {
            temp = (Math.round((temp * 90.0)/5.0))/10.0 + 32.0 as Float
        }
    }
    def units = getTemperatureScale()

    def result = [
        name: 'temperature',
        value: temp,
        unit: units,
        isStateChange:true,
        descriptionText : "${device.displayName} temperature is ${temp}${units}"
    ]
    return result
}


private Map parseHumidity(String description){
    def pct = (description - "humidity: " - "%").trim()

    if (pct.isNumber()) {
        pct =  Math.round(new BigDecimal(pct))

        def result = [
            name: 'humidity',
            value: pct,
            unit: "%",
            isStateChange:true,
            descriptionText : "${device.displayName} Humidity is ${pct}%"
        ]
        return result
    }

    return [:]
}


private Map parseCatchAllMessage(String description) {
    def i
    Map resultMap = [:]
    def cluster = zigbee.parse(description)
    log.debug cluster
    if (cluster) {
        switch(cluster.clusterId) 
        {
            case 0x0000:
                def MsgLength = cluster.data.size();

                // Original Xiaomi CatchAll does not have identifiers, first UINT16 is Battery
                if ((cluster.data.get(0) == 0x02) && (cluster.data.get(1) == 0xFF)) {
                    for (i = 0; i < (MsgLength-3); i++)
                    {
                        if (cluster.data.get(i) == 0x21) // check the data ID and data type
                        {
                            // next two bytes are the battery voltage.
                            resultMap = getBatteryResult((cluster.data.get(i+2)<<8) + cluster.data.get(i+1))
                            break
                        }
                    }
                } else if ((cluster.data.get(0) == 0x01) && (cluster.data.get(1) == 0xFF)) {
                    for (i = 0; i < (MsgLength-3); i++)
                    {
                        if ((cluster.data.get(i) == 0x01) && (cluster.data.get(i+1) == 0x21))  // check the data ID and data type
                        {
                            // next two bytes are the battery voltage.
                            resultMap = getBatteryResult((cluster.data.get(i+3)<<8) + cluster.data.get(i+2))
                            break
                        }
                    }
                }
            break
        }
    }
    return resultMap
}


// Parse raw data on reset button press to retrieve reported battery voltage
private Map parseReadAttr(String description) {
    Map resultMap = [:]

    def cluster = description.split(",").find {it.split(":")[0].trim() == "cluster"}?.split(":")[1].trim()
    def attrId = description.split(",").find {it.split(":")[0].trim() == "attrId"}?.split(":")[1].trim()
    def value = description.split(",").find {it.split(":")[0].trim() == "value"}?.split(":")[1].trim()

	log.debug "${device.displayName} parseReadAttr: cluster: ${cluster}, attrId: ${attrId}, value: ${value}"

    if ((cluster == "0403") && (attrId == "0000")) {
        def result = value[0..3]
        float pressureval = Integer.parseInt(result, 16)

        log.debug "${device.displayName}: Converting ${pressureval} to ${PressureUnits}"

        switch (PressureUnits) {
            case "mbar":
                pressureval = (pressureval/10) as Float
                pressureval = pressureval.round(1);
                break;

            case "kPa":
                pressureval = (pressureval/100) as Float
                pressureval = pressureval.round(2);
                break;

            case "inHg":
                pressureval = (((pressureval/10) as Float) * 0.0295300)
                pressureval = pressureval.round(2);
                break;

            case "mmHg":
                pressureval = (((pressureval/10) as Float) * 0.750062)
                pressureval = pressureval.round(2);
                break;
        }

        log.debug "${device.displayName}: ${pressureval} ${PressureUnits} before applying the pressure offset."

	if (pressOffset == null || pressOffset == "" ) pressOffset = 0    
	if (pressOffset) {
            pressureval = (pressureval + pressOffset)
            pressureval = pressureval.round(2);
        }
        
        resultMap = [
            name: 'pressure',
            value: pressureval,
            unit: "${PressureUnits}",
            isStateChange:true,
            descriptionText : "${device.displayName} Pressure is ${pressureval}${PressureUnits}"
        ]
    } else if (cluster == "0000" && attrId == "0005")  {
        def model = value.split("01FF")[0]
        def data = value.split("01FF")[1]
        
        def modelName = ""
        // Parsing the model
        for (int i = 0; i < model.length(); i+=2) 
        {
            def str = model.substring(i, i+2);
            def NextChar = (char)Integer.parseInt(str, 16);
            modelName = modelName + NextChar
        }
        log.debug "${device.displayName} reported: cluster: ${cluster}, attrId: ${attrId}, value: ${value}, model:${modelName}, data:${data}"
    
        if (data[4..7] == "0121") {
            resultMap = getBatteryResult(Integer.parseInt((data[10..11] + data[8..9]),16))
        }
    }
    return resultMap
}

private Map getBatteryResult(rawValue) {
    def rawVolts = rawValue / 1000

    def minVolts = 2.7
    def maxVolts = 3.3
    def pct = (rawVolts - minVolts) / (maxVolts - minVolts)
    def roundedPct = Math.min(100, Math.round(pct * 100))

    def result = [
        name: 'battery',
        value: roundedPct,
        unit: "%",
        isStateChange:true,
        descriptionText : "${device.displayName} raw battery is ${rawVolts}v"
    ]

    log.debug "${device.displayName}: ${result}"
    if (state.battery != result.value)
    {
        state.battery = result.value
        resetBatteryRuntime()
    }
    return result
}

def refresh(){
    log.debug "${device.displayName}: refreshing"
    return zigbee.readAttribute(0x0000, 0x0001) + zigbee.configureReporting(0x0000, 0x0001, 0x21, 600, 21600, 0x01) + zigbee.configureReporting(0x0402, 0x0000, 0x29, 30, 3600, 0x0064)
}

def configure() {
    state.battery = 0
    // Device-Watch allows 2 check-in misses from device + ping (plus 1 min lag time)
    // enrolls with default periodic reporting until newer 5 min interval is confirmed
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])

    // temperature minReportTime 30 seconds, maxReportTime 5 min. Reporting interval if no activity
    // battery minReport 30 seconds, maxReportTime 6 hrs by default
    return zigbee.readAttribute(0x0000, 0x0001) + zigbee.configureReporting(0x0000, 0x0001, 0x21, 600, 21600, 0x01) + zigbee.configureReporting(0x0402, 0x0000, 0x29, 30, 3600, 0x0064)
}

def resetBatteryRuntime() {
    def now = new Date().format("MMM dd yyyy", location.timeZone)
    sendEvent(name: "batteryRuntime", value: now)
}