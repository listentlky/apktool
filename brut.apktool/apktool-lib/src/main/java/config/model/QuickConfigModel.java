package config.model;

import java.io.Serializable;
import java.util.List;

public class QuickConfigModel implements Serializable {

    private QuickModel quick;

    public QuickModel getQuick() {
        return quick;
    }

    public void setQuick(QuickModel quick) {
        this.quick = quick;
    }

    @Override
    public String toString() {
        return "QuickConfigModel{" +
            "quick=" + quick +
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
