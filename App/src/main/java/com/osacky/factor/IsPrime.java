package com.osacky.factor;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 12/6/13.
 */
public class IsPrime {

    public static List isPrime(long num, long lo, long hi)
    {
        List toRet = new ArrayList();

        if(lo==2 && num%2==0)
        {
            toRet.add(2);
            toRet.add(num>>1);
            Log.e("prime number:", "2");
            Log.e("prime number:", ""+ (num/2));

        }

        if(lo%2==0)
            lo = lo+1;

        for(long i=lo; i<=hi; i+=2)
        {
            if(num%i == 0)
            {
                toRet.add(i);
                toRet.add(num/i);
                Log.d("prime number:",""+ i);
                Log.d("prime number:",""+ (num/i));

            }
        }
        return toRet;
    }

}
