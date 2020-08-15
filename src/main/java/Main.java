import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.*;


public class Main {

    public static void main(String[] args) throws Exception {
        String url = "https://omts.tc.qq.com/AjJ2j5cUjIoa3XJ_u8YgsNDK9gnjeBM2YMi4TYuGT_G0/uwMROfz2r5xgoaQXGdGnC2df64gVTKzl5C_X6A3JOVT0QIb-/8FlBqTJ_ZqSJ8wUsUc2V7TEXm3S5Ea9AywYVi4zg-XRq_ceiBpkkwAsjhlOuzuFLklSiU8vRTnHxX-9hCecdKGATOb58RP9YmtDawi6Bph3KEEN5e-b541uv9HCwkXPlc4u68zD8-xxCHHVWcnW24Q/p0029371vd2.321004.ts.m3u8?ver=4";
        String prefix = "https://omts.tc.qq.com/AjJ2j5cUjIoa3XJ_u8YgsNDK9gnjeBM2YMi4TYuGT_G0/uwMROfz2r5xgoaQXGdGnC2df64gVTKzl5C_X6A3JOVT0QIb-/8FlBqTJ_ZqSJ8wUsUc2V7TEXm3S5Ea9AywYVi4zg-XRq_ceiBpkkwAsjhlOuzuFLklSiU8vRTnHxX-9hCecdKGATOb58RP9YmtDawi6Bph3KEEN5e-b541uv9HCwkXPlc4u68zD8-xxCHHVWcnW24Q/";
        M3U8 data = getData(url, prefix);
        download(data);
        merge(data);
    }

    public static M3U8 getData(String url, String prefix) throws Exception {
        M3U8 ret = new M3U8();
        URL url1 = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        ret.setBasePath(url1.getProtocol().concat("://").concat(url1.getHost()));
        List<String> data = new LinkedList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            } else {
                data.add(prefix.concat(line));
            }
        }
        ret.setDatas(data);
        reader.close();

        return ret;
    }

    public static void download(M3U8 data) throws Exception {
        File dir = new File("");
        List<String> datas = data.getDatas();
        List<String> files = new ArrayList<>();
        for (String d : datas) {
            File file = new File(dir.getAbsolutePath() + File.separator + UUID.randomUUID().toString());
            files.add(file.getPath());
            URL url = new URL(d);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);//会自动创建文件
            int len = 0;
            byte[] buf = new byte[8 * 1024 * 1024];
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);//写入流中
            }
            fos.close();
            inputStream.close();
            file.exists();
        }
        data.setFileUrl(files);
    }

    public static void merge(M3U8 m3u8) throws IOException {
        String absolutePath = new File("").getAbsolutePath();
        File file = new File(absolutePath + File.separator + UUID.randomUUID().toString() + ".ts");
        FileOutputStream fos = new FileOutputStream(file);

        for (String ts : m3u8.getFileUrl()) {
            File file1 = new File(ts);
            IOUtils.copyLarge(new FileInputStream(file1), fos);
            file1.deleteOnExit();
        }
        fos.close();
    }

}
