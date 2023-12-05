package com.neep.neepmeat.init;

import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.network.plc.PLCSyncProgram;

public class NMCommonNetwork
{
    public static void init()
    {
        PLCRobotC2S.init();
        PLCRobotEnterS2C.init();
        PLCSyncProgram.init();
    }
}