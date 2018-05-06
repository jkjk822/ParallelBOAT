package parallelBOAT;

// Enum describing all attributes of an article
public enum Attribute {
    url(0),
    timeDelta(1),
    n_tokens_title(2),
    n_tokens_content(3),
    n_unique_tokens(4),
    n_non_stop_words(5),
    n_non_stop_unique(6),
    num_hrefs(7),
    num_self_hrefs(8),
    num_imgs(9),
    num_videos(10),
    average_token_length(11),
    num_keywords(12),
    data_channel_is_lifestyle(13),
    data_channel_is_entertainment(14),
    data_channel_is_bus(15),
    data_channel_is_socmed(16),
    data_channel_is_tech(17),
    data_channel_is_world(18),
    kw_min_min(19),
    kw_max_min(20),
    kw_avg_min(21),
    kw_min_max(22),
    kw_max_max(23),
    kw_avg_max(24),
    kw_min_avg(25),
    kw_max_avg(26),
    kw_avg_avg(27),
    self_reference_min_shares(28),
    self_reference_max_shares(29),
    self_reference_avg_sharess(30),
    weekday_is_monday(31),
    weekday_is_tuesday(32),
    weekday_is_wednesday(33),
    weekday_is_thursday(34),
    weekday_is_friday(35),
    weekday_is_saturday(36),
    weekday_is_sunday(37),
    is_weekend(38),
    LDA_00(39),
    LDA_01(40),
    LDA_02(41),
    LDA_03(42),
    LDA_04(43),
    global_subjectivity(44),
    global_sentiment_polarity(45),
    global_rate_positive_words(46),
    global_rate_negative_words(47),
    rate_positive_words(48),
    rate_negative_words(49),
    avg_positive_polarity(50),
    min_positive_polarity(51),
    max_positive_polarity(52),
    avg_negative_polarity(53),
    min_negative_polarity(54),
    max_negative_polarity(55),
    title_subjectivity(56),
    title_sentiment_polarity(57),
    abs_title_subjectivity(58),
    abs_title_sentiment_polarity(59),
    shares(60);

    private final int index;

    Attribute(int index) {
        this.index = index;
    }

    // Returns index of an attribute, used to access objects from the raw data array of article
    public int getIndex() {
        return index;
    }

}
