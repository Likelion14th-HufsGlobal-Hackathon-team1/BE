package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.journey.service;

import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.BusinessException;
import Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.error.ErrorCode;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 참 프롬프트가 순백(#FFFFFF) 배경으로 생성되도록 고정돼 있다는 전제 하에,
 * 흰 픽셀을 투명 처리해 프론트가 가방 위에 바로 합성할 수 있는 PNG를 만든다.
 * Cloudinary AI 배경제거는 유료 애드온이라 대신 색상 기반으로 무료 처리.
 */
@Component
public class CharmBackgroundRemover {

    private static final int WHITE_THRESHOLD = 235;
    private static final int FEATHER_BAND = 20;

    public String makeWhiteTransparent(String base64Image) {
        BufferedImage source = decode(base64Image);
        BufferedImage result = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB
        );

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                result.setRGB(x, y, applyWhiteKey(source.getRGB(x, y)));
            }
        }

        return encode(result);
    }

    private BufferedImage decode(String base64Image) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image == null) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지를 읽을 수 없습니다.");
            }
            return image;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지를 읽을 수 없습니다.");
        }
    }

    private int applyWhiteKey(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int minChannel = Math.min(r, Math.min(g, b));

        int alpha;
        if (minChannel >= WHITE_THRESHOLD) {
            alpha = 0;
        } else if (minChannel < WHITE_THRESHOLD - FEATHER_BAND) {
            alpha = 255;
        } else {
            alpha = 255 * (WHITE_THRESHOLD - minChannel) / FEATHER_BAND;
        }

        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private String encode(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 이미지 변환에 실패했습니다.");
        }
    }
}
