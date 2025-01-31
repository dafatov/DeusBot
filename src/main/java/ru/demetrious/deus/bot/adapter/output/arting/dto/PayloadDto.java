package ru.demetrious.deus.bot.adapter.output.arting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Data
@Accessors(chain = true)
public class PayloadDto {
    private final String prompt;
    @JsonProperty(value = "model_id")
    private final String modelId = "waiANINSFWPONYXL_v80";
    private final Integer samples = 1;
    private final Integer height = 512;
    private final Integer width = 768;
    @JsonProperty(value = "negative_prompt")
    private final String negativePrompt = "painting, extra fingers, mutated hands, poorly drawn hands, poorly drawn face, deformed, ugly, " +
        "blurry, bad anatomy, bad proportions, extra limbs, cloned face, skinny, glitchy, double torso, extra arms, extra hands, mangled fingers, " +
        "missing lips, ugly face, distorted face, extra legs";
    private final Integer seed = 0;
    @JsonProperty(value = "lora_ids")
    private final String loraIds = "hentai_anime_style_pony_v2";
    @JsonProperty(value = "lora_weight")
    private final Double loraWeight = 0.7;
    private final String sampler = "Euler a";
    private final Integer steps = 25;
    private final Integer guidance = 7;
    @JsonProperty(value = "clip_skip")
    private final Integer clipSkip = 2;
}
