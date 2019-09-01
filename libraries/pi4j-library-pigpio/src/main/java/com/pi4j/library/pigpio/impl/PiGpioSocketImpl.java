package com.pi4j.library.pigpio.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: PIGPIO Library
 * FILENAME      :  PiGpioSocketImpl.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.library.pigpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static com.pi4j.library.pigpio.PiGpioCmd.*;
import static com.pi4j.library.pigpio.PiGpioConst.DEFAULT_HOST;
import static com.pi4j.library.pigpio.PiGpioConst.DEFAULT_PORT;

public class PiGpioSocketImpl extends PiGpioSocketBase implements PiGpio {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Creates a PiGpio instance using TCP Socket communication for remote I/O access.
     * Connects to a user specified socket hostname/ip address and port.
     *
     * @param host hostname or IP address of the RaspberryPi to connect to via TCP/IP socket.
     * @param port TCP port number of the RaspberryPi to connect to via TCP/IP socket.
     * @throws IOException
     */
    public static PiGpio newInstance(String host, String port) throws IOException {
        return new PiGpioSocketImpl(host, Integer.parseInt(port));
    }

    /**
     * Creates a PiGpio instance using TCP Socket communication for remote I/O access.
     * Connects to a user specified socket hostname/ip address and port.
     *
     * @param host hostname or IP address of the RaspberryPi to connect to via TCP/IP socket.
     * @param port TCP port number of the RaspberryPi to connect to via TCP/IP socket.
     * @throws IOException
     */
    public static PiGpio newInstance(String host, int port) throws IOException {
        return new PiGpioSocketImpl(host, port);
    }

    /**
     * Creates a PiGpio instance using TCP Socket communication for remote I/O access.
     * Connects to a user specified socket hostname/ip address using the default port (8888).
     *
     * @param host hostname or IP address of the RaspberryPi to connect to via TCP/IP socket.
     * @throws IOException
     */
    public static PiGpio newInstance(String host) throws IOException {
        return new PiGpioSocketImpl(host, DEFAULT_PORT);
    }

    /**
     * Creates a PiGpio instance using TCP Socket communication for remote I/O access.
     * Connects to the local system (127.0.0.1) using the default port (8888).
     *
     * @throws IOException
     */
    public static PiGpio newInstance() throws IOException {
        return new PiGpioSocketImpl(DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * DEFAULT PRIVATE CONSTRUCTOR
     *
     * Connects to a user specified socket hostname/ip address and port.
     *
     * @param host hostname or IP address of the RaspberryPi to connect to via TCP/IP socket.
     * @param port TCP port number of the RaspberryPi to connect to via TCP/IP socket.
     * @throws IOException
     */
    private PiGpioSocketImpl(String host, int port) throws IOException {
        super(host, port);
    }

    /**
     * Returns the pigpio library version.
     *
     * @return pigpio version.
     * @throws IOException
     * @throws InterruptedException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioVersion"
     */
    @Override
    public long gpioVersion() throws IOException {
        logger.trace("[VERSION] -> GET VERSION");
        validateReady();
        PiGpioPacket result = sendCommand(PIGPV);
        long version = result.result();
        logger.trace("[VERSION] <- RESULT={}", version);
        return version;
    }

    /**
     * Returns the hardware revision.
     *
     * If the hardware revision can not be found or is not a valid hexadecimal number the function returns 0.
     * The hardware revision is the last few characters on the Revision line of /proc/cpuinfo.
     * The revision number can be used to determine the assignment of GPIO to pins (see gpio).
     *
     * There are at least three types of board.
     *  - Type 1 boards have hardware revision numbers of 2 and 3.
     *  - Type 2 boards have hardware revision numbers of 4, 5, 6, and 15.
     *  - Type 3 boards have hardware revision numbers of 16 or greater.
     *
     *     for "Revision : 0002" the function returns 2.
     *     for "Revision : 000f" the function returns 15.
     *     for "Revision : 000g" the function returns 0.
     *
     * @return hardware revision as raw 32-bit UINT
     * @throws IOException
     * @seel "http://abyz.me.uk/rpi/pigpio/cif.html#gpioHardwareRevision"
     */
    @Override
    public long gpioHardwareRevision() throws IOException {
        logger.trace("[HARDWARE] -> GET REVISION");
        validateReady();
        PiGpioPacket result = sendCommand(HWVER);
        long revision = result.result();
        logger.trace("[HARDWARE] <- REVISION: {}", revision);
        if(revision <= 0) throw new IOException("Hardware revision could not be determined.");
        return revision;
    }

    /**
     * Returns the hardware revision (as hexadecimal string).
     *
     * If the hardware revision can not be found or is not a valid hexadecimal number the function returns 0.
     * The hardware revision is the last few characters on the Revision line of /proc/cpuinfo.
     * The revision number can be used to determine the assignment of GPIO to pins (see gpio).
     *
     * There are at least three types of board.
     *  - Type 1 boards have hardware revision numbers of 2 and 3.
     *  - Type 2 boards have hardware revision numbers of 4, 5, 6, and 15.
     *  - Type 3 boards have hardware revision numbers of 16 or greater.
     *
     *     for "Revision : 0002" the function returns 2.
     *     for "Revision : 000f" the function returns 15.
     *     for "Revision : 000g" the function returns 0.
     *
     * @return hardware revision in hexadecimal string
     * @throws IOException
     * @seel "http://abyz.me.uk/rpi/pigpio/cif.html#gpioHardwareRevision"
     */
    @Override
    public String gpioHardwareRevisionString() throws IOException {
        logger.trace("[HARDWARE] -> GET REVISION (STRING)");
        validateReady();
        PiGpioPacket result = sendCommand(HWVER);
        long revision = result.result();
        String revisionString = Integer.toHexString((int)revision);
        logger.trace("[HARDWARE] <- REVISION (STRING): {}", revisionString);
        return revisionString;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // GPIO IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * Sets or clears resistor pull ups or downs on the GPIO.
     *
     * @param pin gpio pin address
     * @param pud pull-up, pull-down, pull-off
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPullUpDown"
     */
    @Override
    public void gpioSetPullUpDown(int pin, PiGpioPud pud) throws IOException {
        logger.trace("[GPIO::PUD-SET] -> PIN: {}; PUD={}({});", pin, pud.name(), pud.value());
        validateReady();
        validatePin(pin);
        PiGpioPacket result = sendCommand(PUD, pin, pud.value());
        logger.trace("[GPIO::PUD-SET] <- PIN: {}; PUD={}({}); SUCCESS={}", pud.name(), pud.value(), result.success());
        validateResult(result); // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_MODE.
    }

    /**
     * Gets the GPIO mode.
     *
     * @param pin
     * @return pin mode
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetMode"
     */
    @Override
    public PiGpioMode gpioGetMode(int pin) throws IOException {
        logger.trace("[GPIO::MODE-GET] -> PIN: {};", pin);
        validateReady();
        validatePin(pin);
        PiGpioPacket result = sendCommand(MODEG, pin);
        validateResult(result); // Returns the GPIO mode if OK, otherwise PI_BAD_GPIO.
        PiGpioMode mode = PiGpioMode.from(result.result());
        logger.trace("[GPIO::MODE-GET] <- PIN: {}; MODE={}({})", pin, mode.name(), mode.value());
        return mode;
    }

    /**
     * Sets the GPIO mode, typically input or output.
     *
     * gpio: 0-53
     * mode: 0-7
     *
     * @param pin
     * @param mode
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetMode"
     */
    @Override
    public void gpioSetMode(int pin, PiGpioMode mode) throws IOException {
        logger.trace("[GPIO::MODE-SET] -> PIN: {}; MODE={}({});", pin, mode.name(), mode.value());
        validateReady();
        validatePin(pin);
        PiGpioPacket result = sendCommand(MODES, pin, mode.value());
        logger.trace("[GPIO::MODE-SET] <- PIN: {}; MODE={}({}); SUCCESS={}", mode.name(), mode.value(), result.success());
        validateResult(result); // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_PUD.
    }

    /**
     * Reads the GPIO level, on (HIGH) or off (LOW).
     * @param pin gpio pin address
     * @return
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioRead"
     */
    @Override
    public PiGpioState gpioRead(int pin) throws IOException {
        logger.trace("[GPIO::GET] -> PIN: {}", pin);
        validateReady();
        validatePin(pin);
        PiGpioPacket result = sendCommand(READ, pin);
        validateResult(result); // Returns the GPIO level if OK, otherwise PI_BAD_GPIO.
        PiGpioState state = PiGpioState.from(result.p3()); // result value stored in P3
        logger.trace("[GPIO::GET] <- PIN: {} is {}({})", pin, state.name(), state.value());
        return state;
    }

    /**
     * Sets the GPIO level, on (HIGH) or off (LOW).
     *
     * @param pin gpio pin address
     * @param state HIGH or LOW
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioWrite"
     */
    @Override
    public void gpioWrite(int pin, PiGpioState state) throws IOException {
        logger.trace("[GPIO::SET] -> PIN: {}; {}({});", pin, state.name(), state.value());
        validateReady();
        validatePin(pin);
        PiGpioPacket result = sendCommand(WRITE, pin, state.value());
        logger.trace("[GPIO::SET] <- PIN: {}; {}({}); SUCCESS={}",  pin, state.name(), state.value(), result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_LEVEL.
    }

    /**
     * Sets a glitch filter on a GPIO.  (AKA Debounce)
     *
     * Level changes on the GPIO are not reported unless the level has been stable for at
     * least 'steady' microseconds. The level is then reported. Level changes of less
     * than 'steady' microseconds are ignored.
     *
     * This filter affects the GPIO samples returned to callbacks set up with:
     *  - gpioSetAlertFunc
     *  - gpioSetAlertFuncEx
     *  - gpioSetGetSamplesFunc
     *  - gpioSetGetSamplesFuncEx.
     *
     * It does not affect interrupts set up with gpioSetISRFunc, gpioSetISRFuncEx, or
     * levels read by gpioRead, gpioRead_Bits_0_31, or gpioRead_Bits_32_53.
     * Each (stable) edge will be timestamped steady microseconds after it was first detected.
     *
     * @param pin gpio pin address (valid pins are 0-31)
     * @param steady interval in microseconds (valid range: 0-300000)
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGlitchFilter"
     */
    public void gpioGlitchFilter(int pin, int steady) throws IOException {
        logger.trace("[GPIO::GLITCH] -> PIN: {}; INTERVAL: {};", pin, steady);
        validateReady();
        validatePin(pin);
        validateGpioGlitchFilter(steady);
        PiGpioPacket result = sendCommand(FG, pin, steady);
        logger.trace("[GPIO::GLITCH] <- PIN: {}; SUCCESS={}",  pin, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_BAD_FILTER.
    }

    /**
     * Sets a noise filter on a GPIO.
     *
     * Level changes on the GPIO are ignored until a level which has been stable for 'steady'
     * microseconds is detected. Level changes on the GPIO are then reported for 'active'
     * microseconds after which the process repeats.
     *
     * This filter affects the GPIO samples returned to callbacks set up with:
     *  - gpioSetAlertFunc
     *  - gpioSetAlertFuncEx
     *  - gpioSetGetSamplesFunc
     *  - gpioSetGetSamplesFuncEx.     *
     * It does not affect interrupts set up with gpioSetISRFunc, gpioSetISRFuncEx, or
     * levels read by gpioRead, gpioRead_Bits_0_31, or gpioRead_Bits_32_53.
     *
     * Level changes before and after the active period may be reported.
     * Your software must be designed to cope with such reports.
     *
     * @param pin gpio pin address (valid pins are 0-31)
     * @param steady interval in microseconds (valid range: 0-300000)
     * @param active interval in microseconds (valid range: 0-1000000)
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGlitchFilter"
     */
    public void gpioNoiseFilter(int pin, int steady, int active) throws IOException{
        logger.trace("[GPIO::NOISE] -> PIN: {}; INTERVAL: {};", pin, steady);
        validateReady();
        validatePin(pin);
        validateGpioNoiseFilter(steady, active);
        PiGpioPacket result = sendCommand(FN, pin, steady).data(active);
        logger.trace("[GPIO::NOISE] <- PIN: {}; SUCCESS={}",  pin, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO, or PI_BAD_FILTER.
    }


    // *****************************************************************************************************
    // *****************************************************************************************************
    // PWM IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * Starts PWM on the GPIO, dutycycle between 0 (off) and range (fully on). Range defaults to 255.
     *
     * This and the servo functionality use the DMA and PWM or PCM peripherals to control and schedule
     * the pulse lengths and duty cycles.
     *
     * @param pin user_gpio: 0-31
     * @param dutyCycle dutycycle: 0-range
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioPWM"
     */
    @Override
    public void gpioPWM(int pin, int dutyCycle) throws IOException {
        logger.trace("[PWM::SET] -> PIN: {}; DUTY-CYCLE={};", pin, dutyCycle);
        validateReady();
        validateUserPin(pin);
        validateDutyCycle(dutyCycle);
        PiGpioPacket result = sendCommand(PWM, pin, dutyCycle);
        logger.trace("[PWM::SET] <- PIN: {}; DUTY-CYCLE={}; SUCCESS={}",  pin, dutyCycle, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_GPIO or PI_BAD_LEVEL.
    }

    /**
     * Returns the PWM dutycycle setting for the GPIO.
     *
     * For normal PWM the dutycycle will be out of the defined range for the GPIO (see gpioGetPWMrange).
     * If a hardware clock is active on the GPIO the reported dutycycle will be 500000 (500k) out of 1000000 (1M).
     * If hardware PWM is active on the GPIO the reported dutycycle will be out of a 1000000 (1M).
     *
     * Normal PWM range defaults to 255.
     *
     * @param pin user_gpio: 0-31
     * @return Returns between 0 (off) and range (fully on) if OK.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMdutycycle"
     */
    @Override
    public int gpioGetPWMdutycycle(int pin) throws IOException {
        logger.trace("[PWM::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        PiGpioPacket result = sendCommand(GDC, pin);
        var dutyCycle = result.result();
        logger.trace("[PWM::GET] <- PIN: {}; DUTY-CYCLE={}; SUCCESS={}",  pin, dutyCycle, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_NOT_PWM_GPIO.
        return dutyCycle;
    }

    /**
     * Selects the dutycycle range to be used for the GPIO. Subsequent calls to gpioPWM will use a dutycycle
     * between 0 (off) and range (fully on.  If PWM is currently active on the GPIO its dutycycle will be
     * scaled to reflect the new range.
     *
     * The real range, the number of steps between fully off and fully on for each frequency,
     * is given in the following table.
     *
     *  -------------------------------------------------------
     *   #1	   #2	 #3	   #4	 #5	   #6	 #7	    #8	   #9
     *   25,   50,  100,  125,  200,  250,  400,   500,   625,
     *  -------------------------------------------------------
     *  #10   #11   #12   #13   #14   #15    #16   #17    #18
     *  800, 1000, 1250, 2000, 2500, 4000, 5000, 10000, 20000
     *  -------------------------------------------------------
     *
     * The real value set by gpioPWM is (dutycycle * real range) / range.
     *
     * Example
     *   gpioSetPWMrange(24, 2000); // Now 2000 is fully on
     *                              //     1000 is half on
     *                              //      500 is quarter on, etc.
     * @param pin user_gpio: 0-31
     * @param range range: 25-40000
     * @return Returns the real range for the given GPIO's frequency if OK.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPWMrange"
     */
    @Override
    public int gpioSetPWMrange(int pin, int range) throws IOException {
        logger.trace("[PWM-RANGE::SET] -> PIN: {}; RANGE={}", pin, range);
        validateReady();
        validateUserPin(pin);
        //validateDutyCycleRange(range);
        PiGpioPacket result = sendCommand(PRS, pin, range);
        var readRange = result.result();
        logger.trace("[PWM-RANGE::SET] <- PIN: {}; REAL-RANGE={}; SUCCESS={}",  pin, readRange, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_DUTYRANGE.
        return result.result();
    }

    /**
     * Returns the duty-cycle range used for the GPIO if OK.
     * If a hardware clock or hardware PWM is active on the GPIO the reported range will be 1000000 (1M).
     *
     * @param pin user_gpio: 0-31
     * @return duty-cycle range
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMrange"
     */
    @Override
    public int gpioGetPWMrange(int pin) throws IOException {
        logger.trace("[PWM-RANGE::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        PiGpioPacket result = sendCommand(PRG, pin);
        var range = result.result();
        logger.trace("[PWM-RANGE::GET] <- PIN: {}; RANGE={}; SUCCESS={}",  pin, range, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_DUTYRANGE.
        return range;
    }

    /**
     * Returns the real range used for the GPIO if OK.
     * If a hardware clock is active on the GPIO the reported real range will be 1000000 (1M).
     * If hardware PWM is active on the GPIO the reported real range will be approximately 250M
     * divided by the set PWM frequency.
     *
     * @param pin user_gpio: 0-31
     * @return real range used for the GPIO if OK.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMrealRange"
     */
    @Override
    public int gpioGetPWMrealRange(int pin) throws IOException {
        logger.trace("[PWM-REAL-RANGE::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        PiGpioPacket result = sendCommand(PRRG, pin);
        var range = result.result();
        logger.trace("[PWM-REAL-RANGE::GET] <- PIN: {}; RANGE={}; SUCCESS={}",  pin, range, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_DUTYRANGE.
        return range;
    }

    /**
     * Sets the frequency in hertz to be used for the GPIO.
     *
     * If PWM is currently active on the GPIO it will be switched off and then back on at the new frequency.
     * Each GPIO can be independently set to one of 18 different PWM frequencies.
     * The selectable frequencies depend upon the sample rate which may be 1, 2, 4, 5, 8, or 10 microseconds (default 5).
     *
     * The frequencies for each sample rate are:
     *
     *                        Hertz
     *
     *        1: 40000 20000 10000 8000 5000 4000 2500 2000 1600
     *            1250  1000   800  500  400  250  200  100   50
     *
     *        2: 20000 10000  5000 4000 2500 2000 1250 1000  800
     *             625   500   400  250  200  125  100   50   25
     *
     *        4: 10000  5000  2500 2000 1250 1000  625  500  400
     *             313   250   200  125  100   63   50   25   13
     * sample
     *  rate
     *  (us)  5:  8000  4000  2000 1600 1000  800  500  400  320
     *             250   200   160  100   80   50   40   20   10
     *
     *        8:  5000  2500  1250 1000  625  500  313  250  200
     *             156   125   100   63   50   31   25   13    6
     *
     *       10:  4000  2000  1000  800  500  400  250  200  160
     *             125   100    80   50   40   25   20   10    5
     *
     *
     * Example:
     *    gpioSetPWMfrequency(23, 0); // Set GPIO23 to lowest frequency.
     *    gpioSetPWMfrequency(24, 500); // Set GPIO24 to 500Hz.
     *    gpioSetPWMfrequency(25, 100000); // Set GPIO25 to highest frequency.
     *
     * @param pin user_gpio: 0-31
     * @param frequency frequency: >=0
     * @return Returns the numerically closest frequency if OK
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioSetPWMrange"
     */
    @Override
    public int gpioSetPWMfrequency(int pin, int frequency) throws IOException {
        logger.trace("[PWM-FREQ::SET] -> PIN: {}; FREQUENCY={}", pin, frequency);
        validateReady();
        validateUserPin(pin);
        // validateFrequency(frequency); TODO :: IMPLEMENT 'validateFrequency()'
        PiGpioPacket result = sendCommand(PFS, pin, frequency);
        var actualRange = result.result();
        logger.trace("[PWM-FREQ::SET] <- PIN: {}; FREQUENCY={}; SUCCESS={}",  pin, frequency, result.success());
        validateResult(result);  // Returns the numerically closest frequency if OK, otherwise PI_BAD_USER_GPIO.
        return actualRange;
    }

    /**
     * Returns the frequency (in hertz) used for the GPIO
     *
     * For normal PWM the frequency will be that defined for the GPIO by gpioSetPWMfrequency.
     * If a hardware clock is active on the GPIO the reported frequency will be that set by gpioHardwareClock.
     * If hardware PWM is active on the GPIO the reported frequency will be that set by gpioHardwarePWM.
     *
     * Example:
     *    f = gpioGetPWMfrequency(23); // Get frequency used for GPIO23.
     *
     * @param pin user_gpio: 0-31
     * @return Returns the frequency (in hertz) used for the GPIO if OK.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetPWMfrequency"
     */
    @Override
    public int gpioGetPWMfrequency(int pin) throws IOException {
        logger.trace("[PWM-FREQ::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        PiGpioPacket result = sendCommand(PFG, pin);
        var frequency = result.result();
        logger.trace("[PWM-FREQ::GET] <- PIN: {}; FREQUENCY={}; SUCCESS={}",  pin, frequency, result.success());
        validateResult(result);  // Returns the frequency (in hertz) used for the GPIO if OK, otherwise PI_BAD_USER_GPIO.
        return frequency;
    }

    /**
     * Starts hardware PWM on a GPIO at the specified frequency and duty-cycle.
     * Frequencies above 30MHz are unlikely to work.
     *
     * NOTE: Any waveform started by gpioWaveTxSend, or gpioWaveChain will be cancelled.
     *
     * This function is only valid if the pigpio main clock is PCM.
     * The main clock defaults to PCM but may be overridden by a call to gpioCfgClock.
     *
     * The same PWM channel is available on multiple GPIO. The latest frequency and duty-cycle
     * setting will be used by all GPIO which share a PWM channel.
     *
     * The GPIO must be one of the following.
     *
     *   12  PWM channel 0  All models but A and B
     *   13  PWM channel 1  All models but A and B
     *   18  PWM channel 0  All models
     *   19  PWM channel 1  All models but A and B
     *
     *   40  PWM channel 0  Compute module only
     *   41  PWM channel 1  Compute module only
     *   45  PWM channel 1  Compute module only
     *   52  PWM channel 0  Compute module only
     *   53  PWM channel 1  Compute module only
     *
     *
     * The actual number of steps between off and fully on is the integral part of
     * 250M/PWMfreq (375M/PWMfreq for the BCM2711).
     * The actual frequency set is 250M/steps (375M/steps for the BCM2711).
     * There will only be a million steps for a frequency of 250 (375 for the BCM2711). Lower
     * frequencies will have more steps and higher frequencies will have fewer steps.
     * dutyCycle is automatically scaled to take this into account.
     *
     * @param pin a supported hardware PWM pin
     * @param frequency  0 (off) or 1-125M (1-187.5M for the BCM2711)
     * @param dutyCycle  0 (off) to 1000000 (1M)(fully on)
     * @return  Returns 0 if OK, otherwise PI_BAD_GPIO, PI_NOT_HPWM_GPIO, PI_BAD_HPWM_DUTY, PI_BAD_HPWM_FREQ, or PI_HPWM_ILLEGAL.
     */
    @Override
    public void gpioHardwarePWM(int pin, int frequency, int dutyCycle) throws IOException {
        logger.trace("[HW-PWM::SET] -> PIN: {}; FREQUENCY={}; DUTY-CYCLE={}", pin, frequency, dutyCycle);
        validateReady();
        validateUserPin(pin);
        // validateHwPwmFrequency(frequency); TODO :: IMPLEMENT 'validateHwPwmFrequency()'
        PiGpioPacket tx = new PiGpioPacket(HP, pin, frequency).data(dutyCycle);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[HW-PWM::SET] <- PIN: {}; SUCCESS={}",  pin, rx.success());
        validateResult(rx);  // Returns the numerically closest frequency if OK, otherwise PI_BAD_USER_GPIO.
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // SERVO IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * Starts servo pulses on the GPIO, 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise).
     *
     * The range supported by servos varies and should probably be determined by experiment. A value
     * of 1500 should always be safe and represents the mid-point of rotation. You can DAMAGE a servo
     * if you command it to move beyond its limits.
     *
     * The following causes an on pulse of 1500 microseconds duration to be transmitted on GPIO 17 at
     * a rate of 50 times per second. This will command a servo connected to GPIO 17 to rotate to its
     * mid-point.
     *
     * Example:
     *  - gpioServo(17, 1000); // Move servo to safe position anti-clockwise.
     *  - gpioServo(23, 1500); // Move servo to centre position.
     *  - gpioServo(25, 2000); // Move servo to safe position clockwise.
     *
     * OTHER UPDATE RATES:
     * This function updates servos at 50Hz. If you wish to use a different
     * update frequency you will have to use the PWM functions.
     *
     *    PWM Hz      50     100    200    400    500
     *    1E6/Hz   20000   10000   5000   2500   2000
     *
     * Firstly set the desired PWM frequency using gpioSetPWMfrequency.
     * Then set the PWM range using gpioSetPWMrange to 1E6/frequency. Doing this
     * allows you to use units of microseconds when setting the servo pulsewidth.
     *
     * E.g. If you want to update a servo connected to GPIO25 at 400Hz*
     *  - gpioSetPWMfrequency(25, 400);
     *  - gpioSetPWMrange(25, 2500);
     *
     * Thereafter use the PWM command to move the servo, e.g. gpioPWM(25, 1500) will set a 1500 us pulse.
     *
     * @param pin user_gpio: 0-31
     * @param pulseWidth  0, 500-2500
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioServo"
     */
    public void gpioServo(int pin, int pulseWidth) throws IOException{
        logger.trace("[SERVO::SET] -> PIN: {}; PULSE-WIDTH={};", pin, pulseWidth);
        validateReady();
        validateUserPin(pin);
        validatePulseWidth(pulseWidth);
        PiGpioPacket result = sendCommand(SERVO, pin, pulseWidth);
        logger.trace("[SERVO::SET] <- PIN: {}; PULSE-WIDTH={}; SUCCESS={}",  pin, pulseWidth, result.success());
        validateResult(result);  // Returns 0 if OK, otherwise PI_BAD_USER_GPIO or PI_BAD_PULSEWIDTH.
    }

    /**
     * Returns the servo pulse-width setting for the GPIO.
     *
     * @param pin user_gpio: 0-31
     * @return Returns 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise) if OK.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioGetServoPulsewidth"
     */
    public int gpioGetServoPulsewidth(int pin) throws IOException{
        logger.trace("[SERVO::GET] -> PIN: {}", pin);
        validateReady();
        validateUserPin(pin);
        PiGpioPacket result = sendCommand(GPW, pin);
        var pulseWidth = result.result();
        logger.trace("[SERVO::GET] <- PIN: {}; PULSE-WIDTH={}; SUCCESS={}",  pin, pulseWidth, result.success());

        // Returns 0 (off), 500 (most anti-clockwise) to 2500 (most clockwise)
        // if OK, otherwise PI_BAD_USER_GPIO or PI_NOT_SERVO_GPIO.
        validateResult(result);
        return pulseWidth;
    }


    // *****************************************************************************************************
    // *****************************************************************************************************
    // DELAY/SLEEP/TIMER IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * Delays for at least the number of microseconds specified by micros.
     * (Delays of 100 microseconds or less use busy waits.)
     *
     * @param micros micros: the number of microseconds to sleep
     * @return Returns the actual length of the delay in microseconds.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioDelay"
     */
    @Override
    public int gpioDelay(int micros) throws IOException {
        logger.trace("[DELAY] -> MICROS: {}", micros);
        validateReady();
        validateDelayMicroseconds(micros);
        PiGpioPacket result = sendCommand(MICS, (int)micros);
        logger.trace("[DELAY] <- MICROS: {}; SUCCESS={}",  micros, result.success());
        validateResult(result); // Upon success nothing is returned. On error a negative status code will be returned.
        return micros;
    }

    /**
     * Delays for at least the number of milliseconds specified by micros. (between 1 and 60000 <1 minute>)
     *
     * @param millis millis: the number of milliseconds to sleep (between 1 and 60000 <1 minute>)
     * @return Returns the actual length of the delay in milliseconds.
     * @see "http://abyz.me.uk/rpi/pigpio/pigs.html#MILS"
     */
    @Override
    public int gpioDelayMilliseconds(int millis) throws IOException{
        logger.trace("[DELAY] -> MILLIS: {}", millis);
        validateReady();
        validateDelayMilliseconds(millis);
        PiGpioPacket result = sendCommand(MILS, (int)millis);
        logger.trace("[DELAY] <- MILLIS: {}; SUCCESS={}",  millis, result.success());
        validateResult(result); // Upon success nothing is returned. On error a negative status code will be returned.
        return millis;
    }

    /**
     * Returns the current system tick.
     * Tick is the number of microseconds since system boot.
     *
     * As tick is an unsigned 32 bit quantity it wraps around after 2^32 microseconds, which is
     * approximately 1 hour 12 minutes.  You don't need to worry about the wrap around as long as you
     * take a tick (uint32_t) from another tick, i.e. the following code will always provide the
     * correct difference.
     *
     * Example
     *   uint32_t startTick, endTick;
     *   int diffTick;
     *   startTick = gpioTick();
     *
     *   // do some processing
     *   endTick = gpioTick();
     *   diffTick = endTick - startTick;
     *   printf("some processing took %d microseconds", diffTick);
     *
     * @return Returns the current system tick.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#gpioTick"
     */
    @Override
    public long gpioTick() throws IOException {
        logger.trace("[TICK::GET] -> Get current tick");
        validateReady();
        PiGpioPacket tx = new PiGpioPacket(TICK);
        PiGpioPacket rx = sendPacket(tx);
        long tick = Integer.toUnsignedLong(rx.result()); // convert (UInt32) 32-bit unsigned value to long
        logger.trace("[TICK::GET] <- TICK: {}; SUCCESS={}",  tick, rx.success());
        return tick;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // I2C IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * Opens a I2C device on a I2C bus for communications.
     * This returns a handle for the device at the address on the I2C bus.
     * Physically buses 0 and 1 are available on the Pi.
     * Higher numbered buses will be available if a kernel supported bus multiplexor is being used.
     *
     * The GPIO used are given in the following table.
     *         SDA   SCL
     * I2C0     0     1
     * I2C1     2     3
     *
     * @param bus the I2C bus address to open/access for reading and writing. (>=0)
     * @param device the I2C device address to open/access for reading and writing. (0-0x7F)
     * @param flags no flags are currently defined. This parameter should be set to zero.
     * @return Returns a handle (>=0) if OK, otherwise PI_BAD_I2C_BUS, PI_BAD_I2C_ADDR, PI_BAD_FLAGS, PI_NO_HANDLE, or PI_I2C_OPEN_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cOpen"
     */
    @Override
    public int i2cOpen(int bus, int device, int flags) throws IOException {
        logger.trace("[I2C::OPEN] -> Open I2C Bus [{}] and Device [{}]", bus, device);
        validateReady();
        validateI2cBus(bus);
        validateI2cDeviceAddress(device);
        PiGpioPacket tx = new PiGpioPacket(I2CO, bus, device).data(flags);
        PiGpioPacket rx = sendPacket(tx);
        int handle = rx.result();
        logger.trace("[I2C::OPEN] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        // if the open was successful, then we need to cache the I2C handle
        if(rx.success()) {
            System.out.println(">>> " + handle);
            i2cHandles.add(handle);
        }

        // return handle
        return handle;
    }

    /**
     * This closes the I2C device associated with the handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cClose"
     */
    @Override
    public int i2cClose(int handle) throws IOException {
        logger.trace("[I2C::CLOSE] -> HANDLE={}, Close I2C Bus", handle);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(I2CC, handle);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::CLOSE] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        // if the close was successful, then we need to remove the I2C handle from cache
        if(rx.success()) i2cHandles.remove(handle);

        // return result
        return rx.result();
    }

    /**
     * This sends a single bit (in the Rd/Wr bit) to the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param bit 0-1, the value to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteQuick"
     */
    @Override
    public int i2cWriteQuick(int handle, boolean bit) throws IOException {
        logger.trace("[I2C::WRITE] -> HANDLE={}; R/W Bit [{}]", handle, bit ? 1 : 0);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(I2CWQ, handle, bit ? 1 : 0);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This sends a single byte to the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param value raw byte value (0-0xFF) to write to I2C device
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteByte"
     */
    @Override
    public int i2cWriteByte(int handle, byte value) throws IOException {
        logger.trace("[I2C::WRITE] -> HANDLE={}; Byte [{}]", handle, Byte.toUnsignedInt(value));
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(I2CWS, handle, Byte.toUnsignedInt(value));
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This reads a single byte from the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @return Returns the byte read (>=0) if OK, otherwise PI_BAD_HANDLE, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadByte"
     */
    @Override
    public int i2cReadByte(int handle) throws IOException {
        logger.trace("[I2C::READ] -> [{}]; Byte", handle);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(I2CRS, handle);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This writes a single byte to the specified register of the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register i2cReg: 0-255, the register to write
     * @param value raw byte value (0-0xFF) to write to I2C device
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteByteData"
     */
    @Override
    public int i2cWriteByteData(int handle, int register, byte value) throws IOException {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; Byte [{}]", handle ,register, Byte.toUnsignedInt(value));
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CWB, handle, register).data(Byte.toUnsignedInt(value));
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This writes a single 16 bit word to the specified register of the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to write to. (0-255)
     * @param value raw word (2-byte) value (0-0xFFFF) to write to I2C device
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteWordData"
     */
    @Override
    public int i2cWriteWordData(int handle, int register, int value) throws IOException {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; Word [{}]", handle ,register, value);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CWW, handle, register).data(value);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This reads a single byte from the specified register of the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to read from. (0-255)
     * @return Returns the byte read (>=0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadByteData"
     */
    @Override
    public int i2cReadByteData(int handle, int register) throws IOException {
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; Byte", handle ,register);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CRB, handle, register);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This reads a single 16 bit word from the specified register of the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to read from. (0-255)
     * @return Returns the word (2-byte value) read (>=0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadWordData"
     */
    @Override
    public int i2cReadWordData(int handle, int register) throws IOException {
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; Word", handle ,register);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CRW, handle, register);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This writes 16 bits of data to the specified register of the device associated with
     * handle and reads 16 bits of data in return. (in a single transaction)
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to write to and read from. (0-255)
     * @param value raw word (2-byte) value (0-0xFFFF) to write to I2C device
     * @return Returns the word read (>=0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cProcessCall"
     */
    @Override
    public int i2cProcessCall(int handle, int register, int value) throws IOException {
        logger.trace("[I2C::W/R] -> [{}]; Register [{}]; Word [{}]", handle ,register, value);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CPC, handle, register).data(value);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::W/R] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This writes up to 32 bytes to the specified register of the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to write to. (0-255)
     * @param data the array of bytes to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write (maximum 32 bytes supported)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteBlockData"
     */
    @Override
    public int i2cWriteBlockData(int handle, int register, byte[] data, int offset, int length) throws IOException {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; Block [{} bytes]", handle ,register, data.length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, data.length);
        validateHandle(handle);
        validateI2cRegister(register);
        validateI2cBlockLength(length);
        PiGpioPacket tx = new PiGpioPacket(I2CWK, handle, register).data(data, offset, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This reads a block of up to 32 bytes from the specified register of the device associated with handle.
     * The amount of returned data is set by the device.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to read from. (0-255)
     * @param buffer a byte array to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read (>=0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     */
    @Override
    public int i2cReadBlockData(int handle, int register, byte[] buffer, int offset, int length) throws IOException {
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; Block", handle ,register);
        validateReady();
        Objects.checkFromIndexSize(offset, length, buffer.length);
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CRK, handle, register);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        if(rx.success()) {
            int actual = rx.result();
            if(rx.dataLength() < actual) actual = rx.dataLength();
            System.arraycopy(rx.data(), 0, buffer, offset, actual);
        }
        return rx.result();
    }

    /**
     * This writes data bytes to the specified register of the device associated with handle and reads a
     * device specified number of bytes of data in return.
     *
     * The SMBus 2.0 documentation states that a minimum of 1 byte may be sent and a minimum of 1 byte may be received.
     * The total number of bytes sent/received must be 32 or less.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to read from. (0-255)
     * @param write a byte array containing data to write
     * @param writeOffset the starting offset position in the provided byte array to start writing from.
     * @param writeLength the number of bytes to write (maximum 32 bytes supported)
     * @param read a byte array to receive the read data; note the size must be pre-allocated and must be at
     *             is determined by the actual I2C device  (a pre-allocated array/buffer of 32 bytes is safe)
     * @param readOffset the starting offset position in the provided read array/buffer to start copying the data bytes read.
     * @return Returns the number of bytes read (>=0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cBlockProcessCall"
     */
    @Override
    public int i2cBlockProcessCall(int handle, int register,
                                   byte[] write, int writeOffset, int writeLength,
                                   byte[] read, int readOffset) throws IOException {
        logger.trace("[I2C::W/R] -> [{}]; Register [{}]; Block [{} bytes]", handle ,register, writeLength);
        validateReady();
        Objects.checkFromIndexSize(writeOffset, writeLength, write.length);
        validateHandle(handle);
        validateI2cRegister(register);
        validateI2cBlockLength(writeLength);

        // write/read from I2C device
        PiGpioPacket tx = new PiGpioPacket(I2CPK, handle, register).data(write, writeOffset, writeLength);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::W/R] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);

        // copy data bytes to provided "read" array/buffer
        if(rx.success()) {
            int readLength = rx.result();
            if(rx.dataLength() < readLength) readLength = rx.dataLength();

            // make sure the read array has sufficient space to store the bytes returned
            Objects.checkFromIndexSize(readOffset, readLength, read.length);
            System.arraycopy(rx.data(), 0, read, readOffset, readLength);
        }
        return rx.result();
    }

    /**
     * This reads count bytes from the specified register of the device associated with handle .
     * The maximum length of data that can be read is 32 bytes.
     * The minimum length of data that can be read is 1 byte.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to read from. (0-255)
     * @param buffer a byte array (pre-allocated) to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read (1-32)
     * @return Returns the number of bytes read (>0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadI2CBlockData"
     */
    @Override
    public int i2cReadI2CBlockData(int handle, int register, byte[] buffer, int offset, int length) throws IOException{
        logger.trace("[I2C::READ] -> [{}]; Register [{}]; I2C Block [{} bytes]", handle ,register, length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, buffer.length);
        validateHandle(handle);
        validateI2cRegister(register);
        PiGpioPacket tx = new PiGpioPacket(I2CRI, handle, register).data(length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        logger.trace("[I2C::READ] <- DATA SIZE={}",  rx.result());
        logger.trace("[I2C::READ] <- DATA LENGTH={}",  rx.dataLength());
        logger.trace("[I2C::READ] <- BUFFER SIZE={}",  rx.data());
        logger.trace("[I2C::READ] <- OFFSET={}",  offset);

        if(rx.success()) {
            try {
                int actual = rx.result();
                if(rx.dataLength() < actual) actual = rx.dataLength();
                System.arraycopy(rx.data(), 0, buffer, offset, actual);
            }
            catch (ArrayIndexOutOfBoundsException a){
                logger.error(a.getMessage(), a);
            }

        }
        return rx.result();
    }

    /**
     * This writes 1 to 32 bytes to the specified register of the device associated with handle.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param register the I2C register address to write to. (0-255)
     * @param data a byte array containing the data to write to the I2C device register
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the maximum number of bytes to read (1-32)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteI2CBlockData"
     */
    @Override
    public int i2cWriteI2CBlockData(int handle, int register, byte[] data, int offset, int length) throws IOException {
        logger.trace("[I2C::WRITE] -> [{}]; Register [{}]; I2C Block [{} bytes]", handle ,register, data.length);
        validateReady();
        validateHandle(handle);
        validateI2cRegister(register);
        validateI2cBlockLength(data.length);
        PiGpioPacket tx = new PiGpioPacket(I2CWI, handle, register).data(data, offset, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This reads count bytes from the raw device into byte buffer array.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param buffer a byte array (pre-allocated) to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read (1-32)
     * @return Returns number of bytes read (>0) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_READ_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cReadDevice"
     */
    @Override
    public int i2cReadDevice(int handle, byte[] buffer, int offset, int length) throws IOException {
        logger.trace("[I2C::READ] -> [{}]; I2C Raw Read [{} bytes]", handle, length);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(I2CRD, handle, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);
        if(rx.success()) {
            int actual = rx.result();
            if(rx.dataLength() < actual) actual = rx.dataLength();
            System.arraycopy(rx.data(), 0, buffer, offset, actual);
        }
        return rx.result();
    }

    /**
     * This writes the length of bytes from the provided data array to the raw I2C device.
     *
     * @param handle the open I2C device handle; (>=0, as returned by a call to i2cOpen)
     * @param data the array of bytes to write
     * @param offset the starting offset position in the provided array/buffer to start writing from.
     * @param length the number of bytes to write (maximum 32 bytes supported)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_I2C_WRITE_FAILED.
     * @throws IOException
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#i2cWriteDevice"
     */
    @Override
    public int i2cWriteDevice(int handle, byte[] data, int offset, int length) throws IOException {
        logger.trace("[I2C::WRITE] -> [{}]; I2C Raw Write [{} bytes]", handle, data.length);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(I2CWD, handle).data(data, offset, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[I2C::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // SERIAL IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * This function opens a serial device at a specified baud rate and with specified flags.
     * The device name must start with "/dev/tty" or "/dev/serial".
     *
     * @param device the serial device to open (Example: "/dev/ttyAMA0")
     * @param baud  the baud rate in bits per second, see below
     *              The baud rate must be one of 50, 75, 110, 134, 150, 200, 300, 600, 1200,
     *              1800, 2400, 4800, 9600, 19200, 38400, 57600, 115200, or 230400.
     * @param flags  No flags are currently defined. This parameter should be set to zero.
     * @return Returns a handle (>=0) if OK, otherwise PI_NO_HANDLE, or PI_SER_OPEN_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serOpen"
     */
    @Override
    public int serOpen(CharSequence device, int baud, int flags) throws IOException {
        logger.trace("[SERIAL::OPEN] -> Open Serial Port [{}] at Baud Rate [{}]", device, baud);
        validateReady();
        PiGpioPacket tx = new PiGpioPacket(SERO, baud, flags).data(device);
        PiGpioPacket rx = sendPacket(tx);
        int handle = rx.result();
        logger.trace("[SERIAL::OPEN] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        // if the open was successful, then we need to add the SERIAL handle to cache
        if(rx.success()) serialHandles.add(handle);

        // return the handle
        return handle;
    }

    /**
     * This function closes the serial device associated with handle.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serClose"
     */
    @Override
    public int serClose(int handle) throws IOException {
        logger.trace("[SERIAL::CLOSE] -> HANDLE={}, Close Serial Port", handle);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SERC, handle);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SERIAL::CLOSE] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        // if the close was successful, then we need to remove the SERIAL handle from cache
        if(rx.success()) serialHandles.remove(handle);

        // return result
        return rx.result();
    }

    /**
     * This function writes a single byte "value" to the serial port associated with handle.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @param value byte value to write to serial port
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serWriteByte"
     */
    @Override
    public int serWriteByte(int handle, byte value) throws IOException {
        logger.trace("[SERIAL::WRITE] -> HANDLE={}; Byte [{}]", handle, Byte.toUnsignedInt(value));
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SERWB, handle, Byte.toUnsignedInt(value));
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SERIAL::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return 0;
    }

    /**
     * This function reads a byte from the serial port associated with handle.
     * If no data is ready PI_SER_READ_NO_DATA is returned.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @return Returns the read byte (>=0) if OK, otherwise PI_BAD_HANDLE, PI_SER_READ_NO_DATA, or PI_SER_READ_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serReadByte"
     */
    @Override
    public int serReadByte(int handle) throws IOException {
        logger.trace("[SERIAL::READ] -> [{}]; Byte", handle);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SERRB, handle);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SERIAL::READ] <- HANDLE={}; SUCCESS={}",  handle, rx.p3());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This function writes multiple bytes from the buffer array ('data') to the serial
     * port associated with handle.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @param data the array of bytes to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_WRITE_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serWrite"
     */
    @Override
    public int serWrite(int handle, byte[] data, int offset, int length) throws IOException {
        logger.trace("[SERIAL::WRITE] -> [{}]; Serial Write [{} bytes]", handle, data.length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, data.length);
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SERW, handle).data(data, offset, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SERIAL::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This function reads up count bytes from the serial port associated with handle and
     * writes them to the buffer parameter.   If no data is ready, zero is returned.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @param buffer a byte array to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read (>0=) if OK, otherwise PI_BAD_HANDLE, PI_BAD_PARAM, or PI_SER_READ_NO_DATA.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serRead"
     */
    @Override
    public int serRead(int handle, byte[] buffer, int offset, int length) throws IOException {
        logger.trace("[SERIAL::READ] -> [{}]; Serial Read [{} bytes]", handle, length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, buffer.length);
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SERR, handle, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SERIAL::READ] <- HANDLE={}; SUCCESS={}; BYTES-READ={}",  handle, rx.success(), rx.dataLength());
        validateResult(rx, false);
        if(rx.success()) {
            int actual = rx.result();
            if(rx.dataLength() < actual) actual = rx.dataLength();
            System.arraycopy(rx.data(), 0, buffer, offset, actual);
        }
        return rx.result();
    }

    /**
     * This function returns the number of bytes available to be read from the device associated with handle.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @return Returns the number of bytes of data available (>=0) if OK, otherwise PI_BAD_HANDLE.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#serDataAvailable"
     */
    @Override
    public int serDataAvailable(int handle) throws IOException {
        logger.trace("[SERIAL::AVAIL] -> Get number of bytes available to read");
        validateReady();
        PiGpioPacket tx = new PiGpioPacket(SERDA, handle);
        PiGpioPacket rx = sendPacket(tx);
        int available = rx.result();
        logger.trace("[SERIAL::AVAIL] <- HANDLE={}; SUCCESS={}; AVAILABLE={}",  handle, rx.success(), available);
        validateResult(rx, false);
        return available;
    }

    /**
     * This function will drain the current serial receive buffer of any lingering bytes.
     *
     * @param handle the open serial device handle; (>=0, as returned by a call to serOpen)
     * @return Returns the number of bytes of data drained (>=0) if OK, otherwise PI_BAD_HANDLE.
     */
    @Override
    public int serDrain(int handle) throws IOException{
        logger.trace("[SERIAL::DRAIN] -> Drain any remaining bytes in serial RX buffer");
        validateReady();

        // get number of bytes available
        PiGpioPacket tx = new PiGpioPacket(SERDA, handle);
        PiGpioPacket rx = sendPacket(tx);
        validateResult(rx, false);
        int available = rx.result();

        // if any bytes are available, then drain them now
        if(available > 0){
            tx = new PiGpioPacket(SERR, handle, available);
            rx = sendPacket(tx);
            validateResult(rx, false);
        }
        logger.trace("[SERIAL::DRAIN] <- HANDLE={}; SUCCESS={}; DRAINED={}",  handle, rx.success(), rx.result());
        return available;
    }

    // *****************************************************************************************************
    // *****************************************************************************************************
    // SPI IMPLEMENTATION
    // *****************************************************************************************************
    // *****************************************************************************************************

    /**
     * This function opens a SPI device channel at a specified baud rate and with specified flags.
     * Data will be transferred at baud bits per second.
     * The flags may be used to modify the default behaviour of 4-wire operation, mode 0, active low chip select.
     *
     * The Pi has two SPI peripherals: main and auxiliary.
     * The main SPI has two chip selects (channels), the auxiliary has three.
     * The auxiliary SPI is available on all models but the A and B.
     *
     * The GPIO pins used are given in the following table.
     *
     *             MISO    MOSI   SCLK   CE0   CE1   CE2
     *             -------------------------------------
     *   Main SPI    9      10     11      8	 7	   -
     *   Aux SPI    19      20     21     18	17    16
     *
     *
     *  spiChan  : 0-1 (0-2 for the auxiliary SPI)
     *  baud     : 32K-125M (values above 30M are unlikely to work)
     *  spiFlags : see below
     *
     * spiFlags consists of the least significant 22 bits.
     * -----------------------------------------------------------------
     * 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
     *  b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
     * -----------------------------------------------------------------
     *
     * [mm] defines the SPI mode.
     *      (Warning: modes 1 and 3 do not appear to work on the auxiliary SPI.)
     *
     *      Mode POL  PHA
     *      -------------
     *       0    0    0
     *       1    0    1
     *       2    1    0
     *       3    1    1
     *
     * [px] is 0 if CEx is active low (default) and 1 for active high.
     * [ux] is 0 if the CEx GPIO is reserved for SPI (default) and 1 otherwise.
     * [A] is 0 for the main SPI, 1 for the auxiliary SPI.
     * [W] is 0 if the device is not 3-wire, 1 if the device is 3-wire. Main SPI only.
     * [nnnn] defines the number of bytes (0-15) to write before switching the MOSI line to MISO to read data. This field is ignored if W is not set. Main SPI only.
     * [T] is 1 if the least significant bit is transmitted on MOSI first, the default (0) shifts the most significant bit out first. Auxiliary SPI only.
     * [R] is 1 if the least significant bit is received on MISO first, the default (0) receives the most significant bit first. Auxiliary SPI only.
     * [bbbbbb] defines the word size in bits (0-32). The default (0) sets 8 bits per word. Auxiliary SPI only.
     *
     * The spiRead, spiWrite, and spiXfer functions transfer data packed into 1, 2, or 4 bytes according to the word size in bits.
     *  - For bits 1-8 there will be one byte per word.
     *  - For bits 9-16 there will be two bytes per word.
     *  - For bits 17-32 there will be four bytes per word.
     *
     * Multi-byte transfers are made in least significant byte first order.
     * E.g. to transfer 32 11-bit words buf should contain 64 bytes and count should be 64.
     * E.g. to transfer the 14 bit value 0x1ABC send the bytes 0xBC followed by 0x1A.
     * The other bits in flags should be set to zero.
     *
     * @param channel the SPI device/channel to open [0-1 (0-2 for the auxiliary SPI)]
     * @param baud  baud rate in bits per second
     * @param flags  optional flags to define SPI modes and other SPI communication characteristic, see details above.
     * @return Returns a handle (>=0) if OK, otherwise PI_BAD_SPI_CHANNEL, PI_BAD_SPI_SPEED, PI_BAD_FLAGS, PI_NO_AUX_SPI, or PI_SPI_OPEN_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiOpen"
     */
    @Override
    public int spiOpen(int channel, int baud, int flags) throws IOException {
        logger.trace("[SPI::OPEN] -> Open SPI Channel [{}] at Baud Rate [{}]; Flags=[{}]", channel, baud, flags);
        validateReady();
        PiGpioPacket tx = new PiGpioPacket(SPIO, channel, baud).data(flags);
        PiGpioPacket rx = sendPacket(tx);
        int handle = rx.result();
        logger.trace("[SPI::OPEN] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        // if the open was successful, then we need to add the SPI handle to cache
        if(rx.success()) spiHandles.add(handle);

        // return handle
        return handle;
    }

    /**
     * This functions closes the SPI device identified by the handle.
     *
     * @param handle the open SPI device handle; (>=0, as returned by a call to spiOpen)
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiClose"
     */
    @Override
    public int spiClose(int handle) throws IOException {
        logger.trace("[SPI::CLOSE] -> HANDLE={}, Close Serial Port", handle);
        validateReady();
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SPIC, handle);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SPI::CLOSE] <- HANDLE={}; SUCCESS={}",  handle, rx.success());
        validateResult(rx, false);

        // if the close was successful, then we need to remove the SPI handle from cache
        if(rx.success()) spiHandles.remove(handle);

        // return result
        return rx.result();
    }

    /**
     * This function writes multiple bytes from the byte array ('data') to the SPI
     * device associated with the handle from the given offset index to the specified length.
     *
     * @param handle the open SPI device handle; (>=0, as returned by a call to spiOpen)
     * @param data the array of bytes to write
     * @param offset the starting offset position in the provided buffer to start writing from.
     * @param length the number of bytes to write
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    @Override
    public int spiWrite(int handle, byte[] data, int offset, int length) throws IOException {
        logger.trace("[SPI::WRITE] -> [{}]; Serial Write [{} bytes]", handle, data.length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, data.length);
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SPIW, handle).data(data, offset, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SPI::WRITE] <- HANDLE={}; SUCCESS={}", handle, rx.success());
        validateResult(rx, false);
        return rx.result();
    }

    /**
     * This function reads a number of bytes specified by the 'length' parameter from the
     * SPI device associated with the handle and copies them to the 'buffer' byte array parameter.
     * The 'offset' parameter determines where to start copying/inserting read data in the byte array.
     * If no data is ready, zero is returned; otherwise, the number of bytes read is returned.
     *
     * @param handle the open SPI device handle; (>=0, as returned by a call to spiOpen)
     * @param buffer a byte array to receive the read data
     * @param offset the starting offset position in the provided buffer to start copying the data bytes read.
     * @param length the maximum number of bytes to read
     * @return Returns the number of bytes read (>0=) if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED..
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiRead"
     */
    @Override
    public int spiRead(int handle, byte[] buffer, int offset, int length) throws IOException {
        logger.trace("[SPI::READ] -> [{}]; Serial Read [{} bytes]", handle, length);
        validateReady();
        Objects.checkFromIndexSize(offset, length, buffer.length);
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SPIR, handle, length);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SPI::READ] <- HANDLE={}; SUCCESS={}; BYTES-READ={}",  handle, rx.success(), rx.dataLength());
        validateResult(rx, false);
        if(rx.success()) {
            int actual = rx.result();
            if(rx.dataLength() < actual) actual = rx.dataLength();
            System.arraycopy(rx.data(), 0, buffer, offset, actual);
        }
        return rx.result();
    }

    /**
     * This function transfers (writes/reads simultaneously) multiple bytes with the SPI
     * device associated with the handle.  Write data is taken from the 'write' byte array
     * from the given 'writeOffset' index to the specified length ('numberOfBytes').  Data
     * read from the SPI device is then copied to the 'read' byte array at the given 'readOffset'
     * using the same length.  Both the 'write' and 'read' byte arrays must be at least the size
     * of the defined 'numberOfBytes' + their corresponding offsets.
     *
     * @param handle the open SPI device handle; (>=0, as returned by a call to spiOpen)
     * @param write the array of bytes to write to the SPI device
     * @param writeOffset the starting offset position in the provided 'write' buffer to
     *                    start writing to the SPI device from.
     * @param read the array of bytes to store read data in from the SPI device
     * @param readOffset the starting offset position in the provided 'read' buffer to place
     *                   data bytes read from the SPI device.
     * @param numberOfBytes the number of bytes to transfer (write & read))
     * @return Returns 0 if OK, otherwise PI_BAD_HANDLE, PI_BAD_SPI_COUNT, or PI_SPI_XFER_FAILED.
     * @see "http://abyz.me.uk/rpi/pigpio/cif.html#spiWrite"
     */
    @Override
    public int spiXfer(int handle, byte[] write, int writeOffset, byte[] read, int readOffset, int numberOfBytes) throws IOException {
        logger.trace("[SPI::XFER] -> [{}]; Serial Transfer [{} bytes]", handle, numberOfBytes);
        validateReady();
        Objects.checkFromIndexSize(writeOffset, numberOfBytes, write.length);
        Objects.checkFromIndexSize(readOffset, numberOfBytes, read.length);
        validateHandle(handle);
        PiGpioPacket tx = new PiGpioPacket(SPIX, handle).data(write, writeOffset, numberOfBytes);
        PiGpioPacket rx = sendPacket(tx);
        logger.trace("[SPI::XFER] <- HANDLE={}; SUCCESS={}; BYTES-READ={}",  handle, rx.success(), rx.dataLength());
        validateResult(rx, false);
        if(rx.success()) {
            int actual = rx.result();
            if(rx.dataLength() < actual) actual = rx.dataLength();
            System.arraycopy(rx.data(), 0, read, readOffset, actual);
        }
        return rx.result();
    }
}
