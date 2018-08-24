package cn.yuntk.radio.ibook.bean;

import cn.yuntk.radio.ibook.util.StringUtils;

import java.util.List;

/*分类页面数据*/

public class ClassifyListBean extends PageInfo{
    /*
    * ﻿{"book":[
    * {"content":"评书分类",
    * "num":17,
    * "types":[{"title":"单田芳","id":1},{"title":"田连元","id":3}]
    * {"content":"有声小说分类",
    * "num":12,
    * "types":[{"title":"玄幻奇幻","id":46},{"title":"恐怖惊悚","id":14}]
    * {"content":"综合分类","num":13,
    * "types":[{"title":"今日头条","id":40},{"title":"商业财经","id":42}]}]}
     *  */

    private List<ClassifyType> book;

    public List<ClassifyType> getBook() {
        return book;
    }

    public void setBook(List<ClassifyType> book) {
        this.book = book;
    }

    public class ClassifyType{
        private String content;
        private String num;
        private List<ClassifyItem> types;

        public String getContent() {
            return StringUtils.getString(content);
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getNum() {
            return StringUtils.getString(num);
        }

        public void setNum(String num) {
            this.num = num;
        }

        public List<ClassifyItem> getTypes() {
            return types;
        }

        public void setTypes(List<ClassifyItem> types) {
            this.types = types;
        }
    }

    public class ClassifyItem{
        private String title;
        private String id;

        public String getTitle() {
            return StringUtils.getString(title);
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return StringUtils.getString(id);
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
