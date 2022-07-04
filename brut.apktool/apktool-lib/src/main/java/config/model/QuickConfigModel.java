package config.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class QuickConfigModel implements Serializable {

    private QuickModel quick;

    private String oldPackageName;

    private HashMap<String, String> originalPublic;


    public QuickModel getQuick() {
        return quick;
    }

    public void setQuick(QuickModel quick) {
        this.quick = quick;
    }

    public String getOldPackageName() {
        return oldPackageName;
    }

    public void setOldPackageName(String oldPackageName) {
        this.oldPackageName = oldPackageName;
    }

    public HashMap<String, String> getOriginalPublic() {
        return originalPublic;
    }

    public void setOriginalPublic(HashMap<String, String> originalPublic) {
        this.originalPublic = originalPublic;
    }

    @Override
    public String toString() {
        return "QuickConfigModel{" +
            "quick=" + quick +
            ", oldPackageName='" + oldPackageName + '\'' +
            ", originalPublic=" + originalPublic +
            '}';
    }

    public static class QuickModel{
        private List<String> out;

        private List<QuickInfoModel> info;


        public List<String> getOut() {
            return out;
        }

        public void setOut(List<String> out) {
            this.out = out;
        }

        public List<QuickInfoModel> getInfo() {
            return info;
        }

        public void setInfo(List<QuickInfoModel> info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "QuickModel{" +
                "out=" + out +
                ", info=" + info +
                '}';
        }
    }

}
