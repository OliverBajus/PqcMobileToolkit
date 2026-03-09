package cz.monetplus.pqc.benchmark.liboqs.performance.dsa

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libqos_android.Oqs
import com.example.libqos_android.api.model.PqcAlgorithm
import com.example.libqos_android.api.model.SignatureAlgorithm
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestSigKeyGenerationOqs {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test fun benchmarkMlDsa3Keygen() = bench(PqcAlgorithm.Sig.MlDsa3)
    @Test fun benchmarkMlDsa5Keygen() = bench(PqcAlgorithm.Sig.MlDsa5)

    @Test fun benchmarkSlhDsa3FastShaKeygen() = bench(PqcAlgorithm.Sig.SlhDsa3FastSha)
    @Test fun benchmarkSlhDsa5FastShaKeygen() = bench(PqcAlgorithm.Sig.SlhDsa5FastSha)
    @Test fun benchmarkSlhDsa3SmallShaKeygen() = bench(PqcAlgorithm.Sig.SlhDsa3SmallSha)
    @Test fun benchmarkSlhDsa5SmallShaKeygen() = bench(PqcAlgorithm.Sig.SlhDsa5SmallSha)

    @Test fun benchmarkSlhDsa3FastShakeKeygen() = bench(PqcAlgorithm.Sig.SlhDsa3FastShake)
    @Test fun benchmarkSlhDsa5FastShakeKeygen() = bench(PqcAlgorithm.Sig.SlhDsa5FastShake)
    @Test fun benchmarkSlhDsa3SmallShakeKeygen() = bench(PqcAlgorithm.Sig.SlhDsa3SmallShake)
    @Test fun benchmarkSlhDsa5SmallShakeKeygen() = bench(PqcAlgorithm.Sig.SlhDsa5SmallShake)

    @Test fun benchmarkMayo3Keygen() = bench(PqcAlgorithm.Sig.Mayo3)
    //@Test fun benchmarkMayo5Keygen() = bench(PqcAlgorithm.Sig.Mayo5)

    @Test fun benchmarkFalcon5Keygen() = bench(PqcAlgorithm.Sig.Falcon5)
    @Test fun benchmarkFalcon5PaddedKeygen() = bench(PqcAlgorithm.Sig.Falcon5Padded)

    //@Test fun benchmarkCross3RsdpSmallKeygen() = bench(PqcAlgorithm.Sig.Cross3RsdpSmall)
    //@Test fun benchmarkCross5RsdpSmallKeygen() = bench(PqcAlgorithm.Sig.Cross5RsdpSmall)
    @Test fun benchmarkCross3RsdpFastKeygen() = bench(PqcAlgorithm.Sig.Cross3RsdpFast)
    @Test fun benchmarkCross5RsdpFastKeygen() = bench(PqcAlgorithm.Sig.Cross5RsdpFast)
    @Test fun benchmarkCross3RsdpBalancedKeygen() = bench(PqcAlgorithm.Sig.Cross3RsdpBalanced)
    //@Test fun benchmarkCross5RsdpBalancedKeygen() = bench(PqcAlgorithm.Sig.Cross5RsdpBalanced)

    //@Test fun benchmarkCross3RsdpgSmallKeygen() = bench(PqcAlgorithm.Sig.Cross3RsdpgSmall)
    //@Test fun benchmarkCross5RsdpgSmallKeygen() = bench(PqcAlgorithm.Sig.Cross5RsdpgSmall)
    @Test fun benchmarkCross3RsdpgFastKeygen() = bench(PqcAlgorithm.Sig.Cross3RsdpgFast)
    @Test fun benchmarkCross5RsdpgFastKeygen() = bench(PqcAlgorithm.Sig.Cross5RsdpgFast)
    @Test fun benchmarkCross3RsdpgBalancedKeygen() = bench(PqcAlgorithm.Sig.Cross3RsdpgBalanced)
    @Test fun benchmarkCross5RsdpgBalancedKeygen() = bench(PqcAlgorithm.Sig.Cross5RsdpgBalanced)

/*    @Test fun benchmarkSnova3Ps56x25x2Keygen() = bench(PqcAlgorithm.Sig.Snova3Ps56x25x2)
    @Test fun benchmarkSnova3Ps49x11x3Keygen() = bench(PqcAlgorithm.Sig.Snova3Ps49x11x3)
    @Test fun benchmarkSnova3Ps37x8x4Keygen() = bench(PqcAlgorithm.Sig.Snova3Ps37x8x4)
    @Test fun benchmarkSnova3Ps24x5x5Keygen() = bench(PqcAlgorithm.Sig.Snova3Ps24x5x5)
    @Test fun benchmarkSnova5Ps29x6x5Keygen() = bench(PqcAlgorithm.Sig.Snova5Ps29x6x5)
    @Test fun benchmarkSnova5Ps60x10x4Keygen() = bench(PqcAlgorithm.Sig.Snova5Ps60x10x4)*/

    @Test fun benchmarkUov3Keygen() = bench(PqcAlgorithm.Sig.Uov3)
    @Test fun benchmarkUov5Keygen() = bench(PqcAlgorithm.Sig.Uov5)
    @Test fun benchmarkUov3PkcKeygen() = bench(PqcAlgorithm.Sig.Uov3Pkc)
    @Test fun benchmarkUov5PkcKeygen() = bench(PqcAlgorithm.Sig.Uov5Pkc)
    @Test fun benchmarkUov3PkcSkcKeygen() = bench(PqcAlgorithm.Sig.Uov3PkcSkc)
    @Test fun benchmarkUov5PkcSkcKeygen() = bench(PqcAlgorithm.Sig.Uov5PkcSkc)

    private fun bench(alg: SignatureAlgorithm) {
        Oqs.createSignatureManager(alg).use { mgr ->
            benchmarkRule.measureRepeated {
                mgr.generateKeyPair()
            }
        }
    }
}