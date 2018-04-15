package ParallelBoat;

public class Article {

    private Popularity popularity;
    private String url;
    private int timeDelta;
    private int n_tokens_title;
    private int n_tokens_content;
    private double n_unique_tokens;
    private int n_non_stop_words;
    private double n_non_stop_unique_tokens;
    private int num_hrefs;
    private int num_self_hrefs;
    private int num_imgs;
    private int num_videos;
    private double average_token_length;
    private int num_keywords;
    private boolean data_channel_is_lifestyle;
    private boolean data_channel_is_entertainment;
    private boolean data_channel_is_bus;
    private boolean data_channel_is_socmed;
    private boolean data_channel_is_tech;
    private boolean data_channel_is_world;
    private int kw_min_min;
    private int kw_max_min;
    private double kw_avg_min;
    private int kw_min_max;
    private int kw_max_max;
    private double kw_avg_max;
    private double kw_min_avg;
    private double kw_max_avg;
    private double kw_avg_avg;
    private int self_reference_min_shares;
    private int self_reference_max_shares;
    private double self_reference_avg_sharess;
    private boolean weekday_is_monday;
    private boolean weekday_is_tuesday;
    private boolean weekday_is_wednesday;
    private boolean weekday_is_thursday;
    private boolean weekday_is_friday;
    private boolean weekday_is_saturday;
    private boolean weekday_is_sunday;
    private boolean is_weekend;
    private double LDA_00;
    private double LDA_01;
    private double LDA_02;
    private double LDA_03;
    private double LDA_04;
    private double global_subjectivity;
    private double global_sentiment_polarity;
    private double global_rate_positive_words;
    private double global_rate_negative_words;
    private double rate_positive_words;
    private double rate_negative_words;
    private double avg_positive_polarity;
    private double min_positive_polarity;
    private double max_positive_polarity;
    private double avg_negative_polarity;
    private double min_negative_polarity;
    private double max_negative_polarity;
    private double title_subjectivity;
    private double title_sentiment_polarity;
    private double abs_title_subjectivity;
    private double abs_title_sentiment_polarity;
    private int shares;


    public Article(String url, int timeDelta, int n_tokens_title, int n_tokens_content, double n_unique_tokens, int n_non_stop_words, double n_non_stop_unique_tokens, int num_hrefs, int num_self_hrefs, int num_imgs, int num_videos, double average_token_length, int num_keywords, boolean data_channel_is_lifestyle, boolean data_channel_is_entertainment, boolean data_channel_is_bus, boolean data_channel_is_socmed, boolean data_channel_is_tech, boolean data_channel_is_world, int kw_min_min, int kw_max_min, double kw_avg_min, int kw_min_max, int kw_max_max, double kw_avg_max, double kw_min_avg, double kw_max_avg, double kw_avg_avg, int self_reference_min_shares, int self_reference_max_shares, double self_reference_avg_sharess, boolean weekday_is_monday, boolean weekday_is_tuesday, boolean weekday_is_wednesday, boolean weekday_is_thursday, boolean weekday_is_friday, boolean weekday_is_saturday, boolean weekday_is_sunday, boolean is_weekend, double LDA_00, double LDA_01, double LDA_02, double LDA_03, double LDA_04, double global_subjectivity, double global_sentiment_polarity, double global_rate_positive_words, double global_rate_negative_words, double rate_positive_words, double rate_negative_words, double avg_positive_polarity, double min_positive_polarity, double max_positive_polarity, double avg_negative_polarity, double min_negative_polarity, double max_negative_polarity, double title_subjectivity, double title_sentiment_polarity, double abs_title_subjectivity, double abs_title_sentiment_polarity, int shares) {
        this.url = url;
        this.timeDelta = timeDelta;
        this.n_tokens_title = n_tokens_title;
        this.n_tokens_content = n_tokens_content;
        this.n_unique_tokens = n_unique_tokens;
        this.n_non_stop_words = n_non_stop_words;
        this.n_non_stop_unique_tokens = n_non_stop_unique_tokens;
        this.num_hrefs = num_hrefs;
        this.num_self_hrefs = num_self_hrefs;
        this.num_imgs = num_imgs;
        this.num_videos = num_videos;
        this.average_token_length = average_token_length;
        this.num_keywords = num_keywords;
        this.data_channel_is_lifestyle = data_channel_is_lifestyle;
        this.data_channel_is_entertainment = data_channel_is_entertainment;
        this.data_channel_is_bus = data_channel_is_bus;
        this.data_channel_is_socmed = data_channel_is_socmed;
        this.data_channel_is_tech = data_channel_is_tech;
        this.data_channel_is_world = data_channel_is_world;
        this.kw_min_min = kw_min_min;
        this.kw_max_min = kw_max_min;
        this.kw_avg_min = kw_avg_min;
        this.kw_min_max = kw_min_max;
        this.kw_max_max = kw_max_max;
        this.kw_avg_max = kw_avg_max;
        this.kw_min_avg = kw_min_avg;
        this.kw_max_avg = kw_max_avg;
        this.kw_avg_avg = kw_avg_avg;
        this.self_reference_min_shares = self_reference_min_shares;
        this.self_reference_max_shares = self_reference_max_shares;
        this.self_reference_avg_sharess = self_reference_avg_sharess;
        this.weekday_is_monday = weekday_is_monday;
        this.weekday_is_tuesday = weekday_is_tuesday;
        this.weekday_is_wednesday = weekday_is_wednesday;
        this.weekday_is_thursday = weekday_is_thursday;
        this.weekday_is_friday = weekday_is_friday;
        this.weekday_is_saturday = weekday_is_saturday;
        this.weekday_is_sunday = weekday_is_sunday;
        this.is_weekend = is_weekend;
        this.LDA_00 = LDA_00;
        this.LDA_01 = LDA_01;
        this.LDA_02 = LDA_02;
        this.LDA_03 = LDA_03;
        this.LDA_04 = LDA_04;
        this.global_subjectivity = global_subjectivity;
        this.global_sentiment_polarity = global_sentiment_polarity;
        this.global_rate_positive_words = global_rate_positive_words;
        this.global_rate_negative_words = global_rate_negative_words;
        this.rate_positive_words = rate_positive_words;
        this.rate_negative_words = rate_negative_words;
        this.avg_positive_polarity = avg_positive_polarity;
        this.min_positive_polarity = min_positive_polarity;
        this.max_positive_polarity = max_positive_polarity;
        this.avg_negative_polarity = avg_negative_polarity;
        this.min_negative_polarity = min_negative_polarity;
        this.max_negative_polarity = max_negative_polarity;
        this.title_subjectivity = title_subjectivity;
        this.title_sentiment_polarity = title_sentiment_polarity;
        this.abs_title_subjectivity = abs_title_subjectivity;
        this.abs_title_sentiment_polarity = abs_title_sentiment_polarity;
        this.shares = shares;
    }

    public Popularity getPopularity() {
        return popularity;
    }

    public void setPopularity(Popularity popularity) {
        this.popularity = popularity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(int timeDelta) {
        this.timeDelta = timeDelta;
    }

    public int getN_tokens_title() {
        return n_tokens_title;
    }

    public void setN_tokens_title(int n_tokens_title) {
        this.n_tokens_title = n_tokens_title;
    }

    public int getN_tokens_content() {
        return n_tokens_content;
    }

    public void setN_tokens_content(int n_tokens_content) {
        this.n_tokens_content = n_tokens_content;
    }

    public double getN_unique_tokens() {
        return n_unique_tokens;
    }

    public void setN_unique_tokens(double n_unique_tokens) {
        this.n_unique_tokens = n_unique_tokens;
    }

    public int getN_non_stop_words() {
        return n_non_stop_words;
    }

    public void setN_non_stop_words(int n_non_stop_words) {
        this.n_non_stop_words = n_non_stop_words;
    }

    public double getN_non_stop_unique_tokens() {
        return n_non_stop_unique_tokens;
    }

    public void setN_non_stop_unique_tokens(double n_non_stop_unique_tokens) {
        this.n_non_stop_unique_tokens = n_non_stop_unique_tokens;
    }

    public int getNum_hrefs() {
        return num_hrefs;
    }

    public void setNum_hrefs(int num_hrefs) {
        this.num_hrefs = num_hrefs;
    }

    public int getNum_self_hrefs() {
        return num_self_hrefs;
    }

    public void setNum_self_hrefs(int num_self_hrefs) {
        this.num_self_hrefs = num_self_hrefs;
    }

    public int getNum_imgs() {
        return num_imgs;
    }

    public void setNum_imgs(int num_imgs) {
        this.num_imgs = num_imgs;
    }

    public int getNum_videos() {
        return num_videos;
    }

    public void setNum_videos(int num_videos) {
        this.num_videos = num_videos;
    }

    public double getAverage_token_length() {
        return average_token_length;
    }

    public void setAverage_token_length(double average_token_length) {
        this.average_token_length = average_token_length;
    }

    public int getNum_keywords() {
        return num_keywords;
    }

    public void setNum_keywords(int num_keywords) {
        this.num_keywords = num_keywords;
    }

    public boolean isData_channel_is_lifestyle() {
        return data_channel_is_lifestyle;
    }

    public void setData_channel_is_lifestyle(boolean data_channel_is_lifestyle) {
        this.data_channel_is_lifestyle = data_channel_is_lifestyle;
    }

    public boolean isData_channel_is_entertainment() {
        return data_channel_is_entertainment;
    }

    public void setData_channel_is_entertainment(boolean data_channel_is_entertainment) {
        this.data_channel_is_entertainment = data_channel_is_entertainment;
    }

    public boolean isData_channel_is_bus() {
        return data_channel_is_bus;
    }

    public void setData_channel_is_bus(boolean data_channel_is_bus) {
        this.data_channel_is_bus = data_channel_is_bus;
    }

    public boolean isData_channel_is_socmed() {
        return data_channel_is_socmed;
    }

    public void setData_channel_is_socmed(boolean data_channel_is_socmed) {
        this.data_channel_is_socmed = data_channel_is_socmed;
    }

    public boolean isData_channel_is_tech() {
        return data_channel_is_tech;
    }

    public void setData_channel_is_tech(boolean data_channel_is_tech) {
        this.data_channel_is_tech = data_channel_is_tech;
    }

    public boolean isData_channel_is_world() {
        return data_channel_is_world;
    }

    public void setData_channel_is_world(boolean data_channel_is_world) {
        this.data_channel_is_world = data_channel_is_world;
    }

    public int getKw_min_min() {
        return kw_min_min;
    }

    public void setKw_min_min(int kw_min_min) {
        this.kw_min_min = kw_min_min;
    }

    public int getKw_max_min() {
        return kw_max_min;
    }

    public void setKw_max_min(int kw_max_min) {
        this.kw_max_min = kw_max_min;
    }

    public double getKw_avg_min() {
        return kw_avg_min;
    }

    public void setKw_avg_min(double kw_avg_min) {
        this.kw_avg_min = kw_avg_min;
    }

    public int getKw_min_max() {
        return kw_min_max;
    }

    public void setKw_min_max(int kw_min_max) {
        this.kw_min_max = kw_min_max;
    }

    public int getKw_max_max() {
        return kw_max_max;
    }

    public void setKw_max_max(int kw_max_max) {
        this.kw_max_max = kw_max_max;
    }

    public double getKw_avg_max() {
        return kw_avg_max;
    }

    public void setKw_avg_max(double kw_avg_max) {
        this.kw_avg_max = kw_avg_max;
    }

    public double getKw_min_avg() {
        return kw_min_avg;
    }

    public void setKw_min_avg(double kw_min_avg) {
        this.kw_min_avg = kw_min_avg;
    }

    public double getKw_max_avg() {
        return kw_max_avg;
    }

    public void setKw_max_avg(double kw_max_avg) {
        this.kw_max_avg = kw_max_avg;
    }

    public double getKw_avg_avg() {
        return kw_avg_avg;
    }

    public void setKw_avg_avg(double kw_avg_avg) {
        this.kw_avg_avg = kw_avg_avg;
    }

    public int getSelf_reference_min_shares() {
        return self_reference_min_shares;
    }

    public void setSelf_reference_min_shares(int self_reference_min_shares) {
        this.self_reference_min_shares = self_reference_min_shares;
    }

    public int getSelf_reference_max_shares() {
        return self_reference_max_shares;
    }

    public void setSelf_reference_max_shares(int self_reference_max_shares) {
        this.self_reference_max_shares = self_reference_max_shares;
    }

    public double getSelf_reference_avg_sharess() {
        return self_reference_avg_sharess;
    }

    public void setSelf_reference_avg_sharess(double self_reference_avg_sharess) {
        this.self_reference_avg_sharess = self_reference_avg_sharess;
    }

    public boolean isWeekday_is_monday() {
        return weekday_is_monday;
    }

    public void setWeekday_is_monday(boolean weekday_is_monday) {
        this.weekday_is_monday = weekday_is_monday;
    }

    public boolean isWeekday_is_tuesday() {
        return weekday_is_tuesday;
    }

    public void setWeekday_is_tuesday(boolean weekday_is_tuesday) {
        this.weekday_is_tuesday = weekday_is_tuesday;
    }

    public boolean isWeekday_is_wednesday() {
        return weekday_is_wednesday;
    }

    public void setWeekday_is_wednesday(boolean weekday_is_wednesday) {
        this.weekday_is_wednesday = weekday_is_wednesday;
    }

    public boolean isWeekday_is_thursday() {
        return weekday_is_thursday;
    }

    public void setWeekday_is_thursday(boolean weekday_is_thursday) {
        this.weekday_is_thursday = weekday_is_thursday;
    }

    public boolean isWeekday_is_friday() {
        return weekday_is_friday;
    }

    public void setWeekday_is_friday(boolean weekday_is_friday) {
        this.weekday_is_friday = weekday_is_friday;
    }

    public boolean isWeekday_is_saturday() {
        return weekday_is_saturday;
    }

    public void setWeekday_is_saturday(boolean weekday_is_saturday) {
        this.weekday_is_saturday = weekday_is_saturday;
    }

    public boolean isWeekday_is_sunday() {
        return weekday_is_sunday;
    }

    public void setWeekday_is_sunday(boolean weekday_is_sunday) {
        this.weekday_is_sunday = weekday_is_sunday;
    }

    public boolean isIs_weekend() {
        return is_weekend;
    }

    public void setIs_weekend(boolean is_weekend) {
        this.is_weekend = is_weekend;
    }

    public double getLDA_00() {
        return LDA_00;
    }

    public void setLDA_00(double LDA_00) {
        this.LDA_00 = LDA_00;
    }

    public double getLDA_01() {
        return LDA_01;
    }

    public void setLDA_01(double LDA_01) {
        this.LDA_01 = LDA_01;
    }

    public double getLDA_02() {
        return LDA_02;
    }

    public void setLDA_02(double LDA_02) {
        this.LDA_02 = LDA_02;
    }

    public double getLDA_03() {
        return LDA_03;
    }

    public void setLDA_03(double LDA_03) {
        this.LDA_03 = LDA_03;
    }

    public double getLDA_04() {
        return LDA_04;
    }

    public void setLDA_04(double LDA_04) {
        this.LDA_04 = LDA_04;
    }

    public double getGlobal_subjectivity() {
        return global_subjectivity;
    }

    public void setGlobal_subjectivity(double global_subjectivity) {
        this.global_subjectivity = global_subjectivity;
    }

    public double getGlobal_sentiment_polarity() {
        return global_sentiment_polarity;
    }

    public void setGlobal_sentiment_polarity(double global_sentiment_polarity) {
        this.global_sentiment_polarity = global_sentiment_polarity;
    }

    public double getGlobal_rate_positive_words() {
        return global_rate_positive_words;
    }

    public void setGlobal_rate_positive_words(double global_rate_positive_words) {
        this.global_rate_positive_words = global_rate_positive_words;
    }

    public double getGlobal_rate_negative_words() {
        return global_rate_negative_words;
    }

    public void setGlobal_rate_negative_words(double global_rate_negative_words) {
        this.global_rate_negative_words = global_rate_negative_words;
    }

    public double getRate_positive_words() {
        return rate_positive_words;
    }

    public void setRate_positive_words(double rate_positive_words) {
        this.rate_positive_words = rate_positive_words;
    }

    public double getRate_negative_words() {
        return rate_negative_words;
    }

    public void setRate_negative_words(double rate_negative_words) {
        this.rate_negative_words = rate_negative_words;
    }

    public double getAvg_positive_polarity() {
        return avg_positive_polarity;
    }

    public void setAvg_positive_polarity(double avg_positive_polarity) {
        this.avg_positive_polarity = avg_positive_polarity;
    }

    public double getMin_positive_polarity() {
        return min_positive_polarity;
    }

    public void setMin_positive_polarity(double min_positive_polarity) {
        this.min_positive_polarity = min_positive_polarity;
    }

    public double getMax_positive_polarity() {
        return max_positive_polarity;
    }

    public void setMax_positive_polarity(double max_positive_polarity) {
        this.max_positive_polarity = max_positive_polarity;
    }

    public double getAvg_negative_polarity() {
        return avg_negative_polarity;
    }

    public void setAvg_negative_polarity(double avg_negative_polarity) {
        this.avg_negative_polarity = avg_negative_polarity;
    }

    public double getMin_negative_polarity() {
        return min_negative_polarity;
    }

    public void setMin_negative_polarity(double min_negative_polarity) {
        this.min_negative_polarity = min_negative_polarity;
    }

    public double getMax_negative_polarity() {
        return max_negative_polarity;
    }

    public void setMax_negative_polarity(double max_negative_polarity) {
        this.max_negative_polarity = max_negative_polarity;
    }

    public double getTitle_subjectivity() {
        return title_subjectivity;
    }

    public void setTitle_subjectivity(double title_subjectivity) {
        this.title_subjectivity = title_subjectivity;
    }

    public double getTitle_sentiment_polarity() {
        return title_sentiment_polarity;
    }

    public void setTitle_sentiment_polarity(double title_sentiment_polarity) {
        this.title_sentiment_polarity = title_sentiment_polarity;
    }

    public double getAbs_title_subjectivity() {
        return abs_title_subjectivity;
    }

    public void setAbs_title_subjectivity(double abs_title_subjectivity) {
        this.abs_title_subjectivity = abs_title_subjectivity;
    }

    public double getAbs_title_sentiment_polarity() {
        return abs_title_sentiment_polarity;
    }

    public void setAbs_title_sentiment_polarity(double abs_title_sentiment_polarity) {
        this.abs_title_sentiment_polarity = abs_title_sentiment_polarity;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
