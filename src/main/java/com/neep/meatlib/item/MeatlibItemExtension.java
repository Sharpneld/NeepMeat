package com.neep.meatlib.item;

public interface MeatlibItemExtension
{
    default boolean meatlib$supportsGuideLookup()
    {
        return false;
    }
}
