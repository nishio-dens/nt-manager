/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter.hashtag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import twitter.manage.TweetManager;
import twitter.util.MultiSortedMap;
import twitter4j.Status;

/**
 * 指定したキーワードに関係のあるハッシュタグを検索する
 * @author nishio
 */
public class HashtagSearcher {

    //twitter管理クラス
    private TweetManager tweetManager;
    //ハッシュタグのパターン
    private static final String HASHTAG_PATTERN = "#[0-9A-Za-z_]+";
    //検索情報数
    private static final int MAX_SEARCH_NUM = 100;

    /**
     *
     * @param tweetManager
     */
    public HashtagSearcher(TweetManager tweetManager) {
        this.tweetManager = tweetManager;
    }

    /**
     * 検索したワードに関するハッシュタグを出現頻度準に取得
     * @param searchWord
     * @return 見つからなかったらnull, 見つかったらkey=出現回数, value = 出現ワードが出現頻度降順に並べられて出力される
     */
    public MultiSortedMap<Integer, String> getDescendantHashtagCount(String searchWord) {
        MultiSortedMap<Integer, String> counter = null;
        Map<String, Integer> searchWordCounter = calcSearchedHashtagCount(searchWord);
        if( searchWordCounter != null ) {
            //検索ワード見つかった時
            counter = new MultiSortedMap<Integer, String>(new Comparator<Integer>(){

                //keyを降順にソート
                @Override
                public int compare(Integer t, Integer t1) {
                    return t1.compareTo(t);
                }

            });

            Set<String> keyset = searchWordCounter.keySet();
            for( String key : keyset ) {
                //keyとvalueを逆転させる
                Integer val = searchWordCounter.get(key);
                counter.add(val, key);
            }
        }
        return counter;
    }

    /**
     * 指定したワードに関連するハッシュタグをツイッターから取得
     * @return 検索結果が見つからない場合はnullを返す
     *         見つかった場合は，key=検索ワード, val = 出現数
     */
    public Map<String, Integer> calcSearchedHashtagCount(String searchWord) {
        //指定したワードをツイッターから検索
        List<Status> searchResult = this.tweetManager.getSearchResult(MAX_SEARCH_NUM, searchWord);
        if( searchResult == null || searchResult.size() == 0 ) {
            return null;
        }
        //ハッシュタグ出現回数
        Map<String, Integer> counter = new HashMap<String, Integer>();

        for(Status s : searchResult ) {
            //検索してきたワードを含むテキスト
            String message = s.getText();
            if( message != null ) {
                //検索してきた1ツイートのハッシュタグ出現回数を取得
                Map<String, Integer> oneTweet = getHashtagCount(message);
                //出現回数
                Set<String> keyset = oneTweet.keySet();
                
                for(String key : keyset) {
                    //いままでその単語が出現していたかどうか
                    Integer count = counter.get(key);
                    if( count == null ) {
                        count = new Integer(0);
                    }
                    //いままで出現していた単語数に新しく出現した単語数を足す
                    count = count + oneTweet.get(key);
                    counter.put(key, count);
                }
            }
        }

        if( counter.size() == 0 ) {
            return null;
        }
        return counter;
    }

    /**
     * 指定したメッセージに含まれるハッシュタグの出現回数を返す
     * @param message
     * @return key = キーワード, value = 出現回数
     */
    public Map<String, Integer> getHashtagCount(String message) {
        // #で始まる情報
        Pattern userPtn = Pattern.compile(HASHTAG_PATTERN);
        Matcher matcher = userPtn.matcher(message);
        
        // #で始まる情報一覧を抜き出す
        Map<String, Integer> counter = new HashMap<String, Integer>();

        while (matcher.find()) {
            //ハッシュタグを探しカウントしていく
            String str = matcher.group(0);
            Integer val = counter.get(str);
            if( val == null ) {
                val = new Integer(0);
            }
            val = val + 1;
            counter.put(str, val);
        }
        return counter;
    }
}
