import lombok.Data;

import java.util.List;

@Data
public class M3U8 {
    private String basePath;
    private List<String> datas;
    private List<String> fileUrl;

    @Override
    public String toString() {
        return "M3U8{" +
                "basePath='" + basePath + '\'' +
                ", datas=" + datas +
                '}';
    }
}
