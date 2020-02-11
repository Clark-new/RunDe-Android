
package com.bokecc.video.utils;



import androidx.collection.ArrayMap;

import com.bokecc.sdk.mobile.live.logging.ELog;
import com.bokecc.video.R;

import java.util.Map;

public class EmotionUtils {

    private static final String TAG = "EmotionUtils";

    /**
     * 表情类型标志符
     */
    public static final int EMOTION_OLD_TYPE = 0x0001;//经典表情
    public static final int EMOTION_CLASSIC_TYPE = 0x0002;//经典表情
    public static final int EMOTION_ALL_TYPE = 0x0003;//经典表情


    public static ArrayMap<String, Integer> EMPTY_MAP;
    public static ArrayMap<String, Integer> EMOTION_OLD_MAP;
    //经典表情
    public static ArrayMap<String, Integer> EMOTION_CLASSIC_MAP;

    public static ArrayMap<String, Integer> EMOTION_EXTRA_MAP;

    public static ArrayMap<String, Integer> EMOTION_ALL_MAP;

    static {
        EMPTY_MAP = new ArrayMap<>();

        EMOTION_OLD_MAP = new ArrayMap<>();
        EMOTION_ALL_MAP = new ArrayMap<>();
        EMOTION_EXTRA_MAP = new ArrayMap<>();

        EMOTION_OLD_MAP.put("[em2_01]", R.drawable.em2_01);
        EMOTION_OLD_MAP.put("[em2_02]", R.drawable.em2_02);
        EMOTION_OLD_MAP.put("[em2_03]", R.drawable.em2_03);
        EMOTION_OLD_MAP.put("[em2_04]", R.drawable.em2_04);
        EMOTION_OLD_MAP.put("[em2_05]", R.drawable.em2_05);
        EMOTION_OLD_MAP.put("[em2_06]", R.drawable.em2_06);
        EMOTION_OLD_MAP.put("[em2_07]", R.drawable.em2_07);
        EMOTION_OLD_MAP.put("[em2_08]", R.drawable.em2_08);
        EMOTION_OLD_MAP.put("[em2_09]", R.drawable.em2_09);
        EMOTION_OLD_MAP.put("[em2_10]", R.drawable.em2_10);
        EMOTION_OLD_MAP.put("[em2_11]", R.drawable.em2_11);
        EMOTION_OLD_MAP.put("[em2_12]", R.drawable.em2_12);
        EMOTION_OLD_MAP.put("[em2_13]", R.drawable.em2_13);
        EMOTION_OLD_MAP.put("[em2_14]", R.drawable.em2_14);
        EMOTION_OLD_MAP.put("[em2_15]", R.drawable.em2_15);
        EMOTION_OLD_MAP.put("[em2_16]", R.drawable.em2_16);
        EMOTION_OLD_MAP.put("[em2_17]", R.drawable.em2_17);
        EMOTION_OLD_MAP.put("[em2_18]", R.drawable.em2_18);
        EMOTION_OLD_MAP.put("[em2_19]", R.drawable.em2_19);
        EMOTION_OLD_MAP.put("[em2_20]", R.drawable.em2_20);

        EMOTION_CLASSIC_MAP = new ArrayMap<>();
        EMOTION_CLASSIC_MAP.put("[em2_201]", R.drawable.e201);
        EMOTION_CLASSIC_MAP.put("[em2_202]", R.drawable.e202);
        EMOTION_CLASSIC_MAP.put("[em2_203]", R.drawable.e203);
        EMOTION_CLASSIC_MAP.put("[em2_204]", R.drawable.e204);
        EMOTION_CLASSIC_MAP.put("[em2_205]", R.drawable.e205);
        EMOTION_CLASSIC_MAP.put("[em2_206]", R.drawable.e206);
        EMOTION_CLASSIC_MAP.put("[em2_207]", R.drawable.e207);
        EMOTION_CLASSIC_MAP.put("[em2_208]", R.drawable.e208);
        EMOTION_CLASSIC_MAP.put("[em2_209]", R.drawable.e209);
        EMOTION_CLASSIC_MAP.put("[em2_210]", R.drawable.e210);
        EMOTION_CLASSIC_MAP.put("[em2_211]", R.drawable.e211);
        EMOTION_CLASSIC_MAP.put("[em2_212]", R.drawable.e212);
        EMOTION_CLASSIC_MAP.put("[em2_213]", R.drawable.e213);
        EMOTION_CLASSIC_MAP.put("[em2_214]", R.drawable.e214);
        EMOTION_CLASSIC_MAP.put("[em2_215]", R.drawable.e215);
        EMOTION_CLASSIC_MAP.put("[em2_216]", R.drawable.e216);
        EMOTION_CLASSIC_MAP.put("[em2_217]", R.drawable.e217);
        EMOTION_CLASSIC_MAP.put("[em2_218]", R.drawable.e218);
        EMOTION_CLASSIC_MAP.put("[em2_219]", R.drawable.e219);
        EMOTION_CLASSIC_MAP.put("[em2_220]", R.drawable.e220);
        EMOTION_CLASSIC_MAP.put("[em2_221]", R.drawable.e221);
        EMOTION_CLASSIC_MAP.put("[em2_222]", R.drawable.e222);
        EMOTION_CLASSIC_MAP.put("[em2_223]", R.drawable.e223);
        EMOTION_CLASSIC_MAP.put("[em2_224]", R.drawable.e224);
        EMOTION_CLASSIC_MAP.put("[em2_225]", R.drawable.e225);
        EMOTION_CLASSIC_MAP.put("[em2_226]", R.drawable.e226);
        EMOTION_CLASSIC_MAP.put("[em2_227]", R.drawable.e227);
        EMOTION_CLASSIC_MAP.put("[em2_228]", R.drawable.e228);
        EMOTION_CLASSIC_MAP.put("[em2_229]", R.drawable.e229);
        EMOTION_CLASSIC_MAP.put("[em2_230]", R.drawable.e230);
        EMOTION_CLASSIC_MAP.put("[em2_231]", R.drawable.e231);
        EMOTION_CLASSIC_MAP.put("[em2_232]", R.drawable.e232);
        EMOTION_CLASSIC_MAP.put("[em2_233]", R.drawable.e233);
        EMOTION_CLASSIC_MAP.put("[em2_234]", R.drawable.e234);
        EMOTION_CLASSIC_MAP.put("[em2_235]", R.drawable.e235);
        EMOTION_CLASSIC_MAP.put("[em2_236]", R.drawable.e236);
        EMOTION_CLASSIC_MAP.put("[em2_237]", R.drawable.e237);
        EMOTION_CLASSIC_MAP.put("[em2_238]", R.drawable.e238);
        EMOTION_CLASSIC_MAP.put("[em2_239]", R.drawable.e239);
        EMOTION_CLASSIC_MAP.put("[em2_240]", R.drawable.e240);
        EMOTION_CLASSIC_MAP.put("[em2_241]", R.drawable.e241);
        EMOTION_CLASSIC_MAP.put("[em2_242]", R.drawable.e242);
        EMOTION_CLASSIC_MAP.put("[em2_243]", R.drawable.e243);
        EMOTION_CLASSIC_MAP.put("[em2_244]", R.drawable.e244);
        EMOTION_CLASSIC_MAP.put("[em2_245]", R.drawable.e245);
        EMOTION_CLASSIC_MAP.put("[em2_246]", R.drawable.e246);
        EMOTION_CLASSIC_MAP.put("[em2_247]", R.drawable.e247);
        EMOTION_CLASSIC_MAP.put("[em2_248]", R.drawable.e248);
        EMOTION_CLASSIC_MAP.put("[em2_249]", R.drawable.e249);
        EMOTION_CLASSIC_MAP.put("[em2_250]", R.drawable.e250);
        EMOTION_CLASSIC_MAP.put("[em2_251]", R.drawable.e251);
        EMOTION_CLASSIC_MAP.put("[em2_252]", R.drawable.e252);
        EMOTION_CLASSIC_MAP.put("[em2_253]", R.drawable.e253);
        EMOTION_CLASSIC_MAP.put("[em2_254]", R.drawable.e254);
        EMOTION_CLASSIC_MAP.put("[em2_255]", R.drawable.e255);
        EMOTION_CLASSIC_MAP.put("[em2_256]", R.drawable.e256);
        EMOTION_CLASSIC_MAP.put("[em2_257]", R.drawable.e257);
        EMOTION_CLASSIC_MAP.put("[em2_258]", R.drawable.e258);
        EMOTION_CLASSIC_MAP.put("[em2_259]", R.drawable.e259);
        EMOTION_CLASSIC_MAP.put("[em2_260]", R.drawable.e260);
        EMOTION_CLASSIC_MAP.put("[em2_261]", R.drawable.e261);
        EMOTION_CLASSIC_MAP.put("[em2_262]", R.drawable.e262);
        EMOTION_CLASSIC_MAP.put("[em2_263]", R.drawable.e263);
        EMOTION_CLASSIC_MAP.put("[em2_264]", R.drawable.e264);
        EMOTION_CLASSIC_MAP.put("[em2_265]", R.drawable.e265);
        EMOTION_CLASSIC_MAP.put("[em2_266]", R.drawable.e266);
        EMOTION_CLASSIC_MAP.put("[em2_267]", R.drawable.e267);
        EMOTION_CLASSIC_MAP.put("[em2_268]", R.drawable.e268);
        EMOTION_CLASSIC_MAP.put("[em2_269]", R.drawable.e269);
        EMOTION_CLASSIC_MAP.put("[em2_270]", R.drawable.e270);
        EMOTION_CLASSIC_MAP.put("[em2_271]", R.drawable.e271);
        EMOTION_CLASSIC_MAP.put("[em2_272]", R.drawable.e272);
        EMOTION_CLASSIC_MAP.put("[em2_273]", R.drawable.e273);
        EMOTION_CLASSIC_MAP.put("[em2_274]", R.drawable.e274);
        EMOTION_CLASSIC_MAP.put("[em2_275]", R.drawable.e275);
        EMOTION_CLASSIC_MAP.put("[em2_276]", R.drawable.e276);
        EMOTION_CLASSIC_MAP.put("[em2_277]", R.drawable.e277);
        EMOTION_CLASSIC_MAP.put("[em2_278]", R.drawable.e278);
        EMOTION_CLASSIC_MAP.put("[em2_279]", R.drawable.e279);
        EMOTION_CLASSIC_MAP.put("[em2_280]", R.drawable.e280);
        EMOTION_CLASSIC_MAP.put("[em2_281]", R.drawable.e281);
        EMOTION_CLASSIC_MAP.put("[em2_282]", R.drawable.e282);
        EMOTION_CLASSIC_MAP.put("[em2_283]", R.drawable.e283);
        EMOTION_CLASSIC_MAP.put("[em2_284]", R.drawable.e284);
        EMOTION_CLASSIC_MAP.put("[em2_285]", R.drawable.e285);
        EMOTION_CLASSIC_MAP.put("[em2_286]", R.drawable.e286);
        EMOTION_CLASSIC_MAP.put("[em2_287]", R.drawable.e287);
        EMOTION_CLASSIC_MAP.put("[em2_288]", R.drawable.e288);
        EMOTION_CLASSIC_MAP.put("[em2_289]", R.drawable.e289);
        EMOTION_CLASSIC_MAP.put("[em2_290]", R.drawable.e290);
        EMOTION_CLASSIC_MAP.put("[em2_291]", R.drawable.e291);
        EMOTION_CLASSIC_MAP.put("[em2_292]", R.drawable.e292);
        EMOTION_CLASSIC_MAP.put("[em2_293]", R.drawable.e293);
        EMOTION_CLASSIC_MAP.put("[em2_294]", R.drawable.e294);
        EMOTION_CLASSIC_MAP.put("[em2_295]", R.drawable.e295);
        EMOTION_CLASSIC_MAP.put("[em2_296]", R.drawable.e296);
        EMOTION_CLASSIC_MAP.put("[em2_297]", R.drawable.e297);
        EMOTION_CLASSIC_MAP.put("[em2_298]", R.drawable.e298);
        EMOTION_CLASSIC_MAP.put("[em2_299]", R.drawable.e299);
        EMOTION_CLASSIC_MAP.put("[em2_300]", R.drawable.e300);


        EMOTION_EXTRA_MAP.put("[em2_q1]", R.drawable.q_one);
        EMOTION_EXTRA_MAP.put("[em2_q2]", R.drawable.q_two);
        EMOTION_ALL_MAP.putAll((Map<? extends String, ? extends Integer>) EMOTION_OLD_MAP);
        EMOTION_ALL_MAP.putAll((Map<? extends String, ? extends Integer>) EMOTION_CLASSIC_MAP);
        EMOTION_ALL_MAP.putAll((Map<? extends String, ? extends Integer>) EMOTION_EXTRA_MAP);
    }

    /**
     * 根据名称获取当前表情图标R值
     *
     * @param EmotionType 表情类型标志符
     * @param imgName     名称
     */
    public static int getImgByName(int EmotionType, String imgName) {
        Integer integer = null;
        switch (EmotionType) {
            case EMOTION_OLD_TYPE:
                integer = EMOTION_OLD_MAP.get(imgName);
                break;
            case EMOTION_CLASSIC_TYPE:
                integer = EMOTION_CLASSIC_MAP.get(imgName);
                break;
            case EMOTION_ALL_TYPE:
                integer = EMOTION_ALL_MAP.get(imgName);
            default:
                ELog.e(TAG, "the emojiMap is null!! Handle Yourself ");
                break;
        }
        return integer == null ? -1 : integer;
    }

    /**
     * 根据类型获取表情数据
     */
    public static ArrayMap<String, Integer> getEmojiMap(int EmotionType) {
        ArrayMap EmojiMap = null;
        switch (EmotionType) {
            case EMOTION_OLD_TYPE:
                EmojiMap = EMOTION_OLD_MAP;
                break;
            case EMOTION_CLASSIC_TYPE:
                EmojiMap = EMOTION_CLASSIC_MAP;
                break;
            default:
                EmojiMap = EMPTY_MAP;
                break;
        }
        return EmojiMap;
    }
}
