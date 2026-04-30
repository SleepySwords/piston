package dev.sleepyswords.piston.noise

import kotlin.math.floor

// Copied from: https://mrl.cs.nyu.edu/~perlin/noise/
//              https://adrianb.io/2014/08/09/perlinnoise.html
object Noise {
    private val permutation: IntArray = intArrayOf(151,160,137,91,90,15,
        131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
        190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
        88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
        77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
        102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
        135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
        5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
        223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
        129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
        251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
        49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
        138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    );
    private val p: IntArray = IntArray(512).apply {
        for (i in 0..255) {
            this[i] = permutation[i]
            this[i + 256] = permutation[i]
        }
    }

    fun perlin(x: Double, y: Double, z: Double): Double {
        val xi = floor(x).toInt() and 255
        val yi = floor(y).toInt() and 255
        val zi = floor(z).toInt() and 255
        val xf = x - floor(x)
        val yf = y - floor(y)
        val zf = z - floor(z)
        val u = fade(xf)
        val v = fade(yf)
        val w = fade(zf)

        val a  = p[xi] + yi
        val aa = p[a] + zi
        val ab = p[a + 1] + zi

        val b  = p[xi + 1] + yi
        val ba = p[b] + zi
        val bb = p[b + 1] +zi

        return lerp(w, lerp(v, lerp(u, grad(p[aa  ], xf  , yf  , zf   ),
                                       grad(p[ba  ], xf-1, yf  , zf   )),
                               lerp(u, grad(p[ab  ], xf  , yf-1, zf   ),
                                       grad(p[bb  ], xf-1, yf-1, zf   ))),
                       lerp(v, lerp(u, grad(p[aa+1], xf  , yf  , zf-1 ),
                                       grad(p[ba+1], xf-1, yf  , zf-1 )),
                               lerp(u, grad(p[ab+1], xf  , yf-1, zf-1 ),
                                       grad(p[bb+1], xf-1, yf-1, zf-1 ))));
    }

    fun grad(hash: Int, x: Double, y: Double, z: Double): Double
    {
        val h = hash and 15;
        val u: Double = if (h < 8) x else y

        val v = if(h < 4 /* 0b0100 */) y
                else if(h == 12 /* 0b1100 */ || h == 14 /* 0b1110*/) x
                else z

        return (if ((h and 1) == 0) u else -u)+(if ((h and 2) == 0) v else -v)
    }

    fun fade(t: Double): Double {
        return t * t * t * (t * (t * 6 - 15) + 10)
    }

    fun lerp( t: Double, a: Double, b: Double): Double {
        return a + (b - a) * t
    }

    fun octavePerlin(x: Double, y: Double, z: Double, octaves: Int, persistence: Double): Double {
        var total = 0.0
        var frequency = 1.0
        var amplitude = 1.0
        var maxValue = 0.0

        for (i in 0 until octaves) {
            total += perlin(x * frequency, y * frequency, z * frequency) * amplitude
            maxValue += amplitude
            amplitude *= persistence

            frequency *= 2
        }
        return total / maxValue
    }
}
