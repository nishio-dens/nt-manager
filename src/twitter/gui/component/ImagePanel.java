package twitter.gui.component;

/**
 * Copyright (c) 2005-2007 tomtom@kuroneko
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     Neither the name of the tomtom@kuroneko nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * イメージを背景に描画したパネルを作成する。<br>
 * イメージを張り付けたJLabelとの違いはパネルのサイズの変更にともない
 * パネル全体を埋めるようにイメージのサイズも変更される。<br>
 * イメージのサイズ変更はパネルのサイズに拡大、縮小されるタイプと、
 * テクスチャを使って並べてパネルを埋めるように描画するタイプがある。<br>
 *
 * 直列化の際にはフィールドimageやtextureはtransientに
 * なっているがイメージがロードされていれば(例えば１度でもパネルに
 * 表示されていれば)直列化される。<br>
 *
 * XMLEncoderではうまく永続化できません。<br>
 *
 * イメージやテクスチャを設定するメソッドで引数のImageやImageIconが
 * 不正な場合はimageプロパティやtextureプロパティはnullに設定され
 * 背景は表示されない。<br>
 * 不正なImageオブジェクトやImageIconオブジェクトとは
 * <pre>
 * Image img = Toolkit.getDefaultToolkit().createImage("not_existent_file");
 * ImageIcon imageIcon = new ImageIcon("*:@/---hoge.png");
 * </pre>
 * 上のコードのように存在しないファイルやイメージファイル以外、あるいは
 * サポートされていないイメージファイルなどを引数して作成された
 * ImageオブジェクトやImageIconオブジェクトを指す。<br>
 * これらのImageオブジェクトやImageIconオブジェクトは生成時に
 * 例外などは発生せず、イメージのロード時にも例外は発生しない。<br>
 * このクラスでもこれらの正しいイメージを保持していない
 * ImageオブジェクトやImageIconオブジェクトを扱う場合に例外などは
 * スローしません。（単に背景を表示しないだけです）
 */
public class ImagePanel extends JPanel {

    /**
     * パネルのサイズにあわせて拡大縮小して背景として使用するイメージ
     */
    transient protected Image image;

    /**
     * テクスチャを並べてパネルを覆って背景とする場合に使用する
     * TexturePaintオブジェクト
     */
    transient protected TexturePaint texture;

    /**
     * 最後に描画した際のパネルサイズを保持するRectangleオブジェクト
     */
    protected Rectangle rect = new Rectangle();

    /**
     * 前回描画に使用した背景イメージのキャッシュ <br>
     */
    transient protected Image cachedImage;

    /**
     * デフォルトコンストラクタ<br>
     * イメージやテクスチャを設定しない場合はただの透明なJPanelとなる。
     */
    public ImagePanel() {
        setOpaque(false);
    }

    /**
     * 引数のTexturePaintオブジェクトが持つテクスチャを並べて
     * 背景とするImagePanelインスタンスを作る。<br>
     * イメージファイルなどからではなく、自分で作成したテクスチャを
     * 使用する場合などにこのコンストラクタを使用する。<br>
     *
     * @param texture テクスチャで使用される矩形のイメージをもつ
     *                TexturePaintオブジェクト
     *
     * @exception IllegalArgumentException  引数がnullの場合
     */
    public ImagePanel(TexturePaint texture) {
        if (texture == null) {
            throw new IllegalArgumentException("argument is null !");
        }
        this.texture = texture;
        setOpaque(false);
    }

    /**
     * 引数のイメージを背景にしたImagePanelインスタンスを作成する。<br>
     *
     * 引数のisTextureがtrueの場合は引数のイメージから
     * テクスチャを生成し、それをパネルを埋めるように並べて描画する。<br>
     * この場合にはgetTextureメソッドは引数のイメージを基に
     * 生成され設定されているTexturePaintオブジェクトを返す。
     * （引数のImageオブジェクトがimageプロパティとして設定されたりは
     * しないのでgetImageメソッドはnullを返す）<br>
     *
     * 引数のisTextureがfalseの場合は引数のイメージを拡大縮小させて
     * パネル全体を覆うように表示させる。<br>
     * この場合にはgetImageメソッドは引数のImageオブジェクトを返すように
     * なる。<br>
     *
     * @param img イメージを格納したImageオブジェクト
     * @param isTexture 引数のイメージをテクスチャとしてパネル全体に
     *                   並べて背景とする場合にはtrueを指定し、
     *                   引数のイメージを拡大縮小させてパネル全体に
     *                   表示する場合にはfalseを指定する。
     * @exception IllegalArgumentException  引数のイメージがnullの場合
     */
    public ImagePanel(Image img, boolean isTexture) {
        setBG(img, isTexture);
        setOpaque(false);
    }

    /**
     * 引数のイメージファイルのパス(カレントディレクトリならファイル名)から
     * イメージを取得してそれをもとに作成した背景をもつ
     * ImagePanelインスタンスを作る<br>
     *
     * 引数のisTextureがtrueの場合はパスから取得したイメージから
     * テクスチャを生成し、それをパネルを埋めるように並べて描画する。<br>
     * この場合にはgetTextureメソッドはイメージを基に作成して
     * 設定されているTexturePaintオブジェクトを返す。<br>
     *
     * 引数のisTextureがfalseの場合はパスから取得したイメージを
     * 拡大縮小させてパネル全体を覆うように表示させる。<br>
     * この場合にはgetImageメソッドはパスから生成したImageオブジェクトを
     * 返すようになる。<br>
     *
     *
     * @param filePath  イメージファイルへのパス
     * @param isTexture 引数のイメージをテクスチャとしてパネル全体に
     *                  並べて背景とする場合にはtrueを指定し、
     *                  引数のイメージを拡大縮小させてパネル全体に
     *                  表示する場合にはfalseを指定する。
     * @exception IllegalArgumentException filePathがnullや空の文字列の場合
     */
    public ImagePanel(String filePath, boolean isTexture) {
        if ((filePath == null) || (filePath.length() == 0)) {
            throw new IllegalArgumentException(
                                "filePath is null or empty !");
        }

        // 存在しないファイルの場合でもここで例外は発生せず、
        // imageIconもnullにはならない。
        ImageIcon imageIcon = new ImageIcon(filePath);
        setBG(imageIcon, isTexture);
        setOpaque(false);
    }

    /**
     * 引数のImageIconオブジェクトのもつイメージを背景にした
     * ImagePanelインスタンスを作成する。<br>
     *
     * 引数のisTextureがtrueの場合は引数のImageIconオブジェクトの保持する
     * イメージからテクスチャを生成し、それをパネルを埋めるように
     * 並べて描画する。<br>
     * この場合にはgetTextureメソッドは引数から取得したイメージを基に
     * 生成されたTexturePaintオブジェクトを返す。
     * （getImageメソッドはnullを返す）<br>
     *
     * 引数のisTextureがfalseの場合は引数のImageIconオブジェクトの保持する
     * イメージを拡大縮小させてパネル全体を覆うように表示させる。<br>
     * この場合にはgetImageメソッドは引数のImageオブジェクトを返すように
     * なる。<br>
     *
     * @param imageIcon イメージを格納したImageIconオブジェクト
     * @param drawType  DrawType.RESIZE(拡大、縮小)もしくは
     *                  DrawType.PATTERN(テクスチャ)
     *
     * @exception  IllegalArgumentException  引数のImageIconオブジェクトが
     *                                       nullの場合
     */
    public ImagePanel(ImageIcon imageIcon, boolean isTexture) {
        setBG(imageIcon, isTexture);
        setOpaque(false);
    }

    /**
     * パネルの背景イメージを設定する。<br>
     * 引数のイメージはパネルのサイズに拡大、縮小して表示される。<br>
     * テクスチャを設定している場合には、このメソッドを呼び出した際に
     * 破棄されるので、その参照が必要な場合にはこのメソッドの前に
     * getTextureメソッドで取得しておく。<br>
     * このメソッドはrepaintメソッドによりパネル全体の再描画を行う。<br>
     * 引数のImageオブジェクトがnullの場合にはIllegalArgumentExceptionを
     * スローする。<br>
     *
     * @param img パネルサイズに拡大、縮小して背景とするImageオブジェクト
     * @exception  IllegalArgumentException  引数がnullの場合
     */
    public void setImage(Image img) {
        if (img == null) {
            throw new IllegalArgumentException("argument is null !");
        }
        if (this.image == img) {
            return;
        }
        setBG(img, false);
        texture = null;
        cachedImage = null;
        repaint();
    }

    /**
     * 現在設定されているパネルサイズに拡大、縮小して背景とする
     * Imageオブジェクトを返す。<br>
     * 設定されていなければnullを返す。
     *
     * @return 拡大、縮小によって背景となるImageオブジェクト
     */
    public Image getImage() {
        return image;
    }

    /**
     * 引数のテクスチャをパネルに並べて背景とする。<br>
     * パネルサイズに拡大縮小して背景とするImageオブジェクトが
     * 設定されている場合には、このメソッドを呼び出した際に
     * 破棄されるので、その参照が必要な場合にはgetImageメソッドで
     * 取得してからこのメソッドを呼び出す。<br>
     * このメソッドはrepaintメソッドによりパネル全体の再描画を行う。<br>
     * 引数のTexturePaintオブジェクトがnullの場合には
     * IllegalArgumentExceptionをスローする。<br>
     *
     * @param texture パネルに並べて背景とするTexturePaintオブジェクト
     * @exception  IllegalArgumentException  引数がnullの場合
     */
    public void setTexture(TexturePaint texture) {
        if (texture == null) {
            throw new IllegalArgumentException("argument is null !");
        }
        if (this.texture == texture) {
            return;
        }
        this.texture = texture;
        image = null;
        cachedImage = null;
        repaint();
    }

    /**
     * 引数のImgeIconをもとにテクスチャを作成して背景とする。<br>
     * パネルサイズに拡大縮小して背景とするImageオブジェクトが
     * 設定されている場合には、このメソッドを呼び出した際に
     * 破棄されるので、その参照が必要な場合にはgetImageメソッドで
     * 取得してからこのメソッドを呼び出す。<br>
     * このメソッドはrepaintメソッドによりパネル全体の再描画を行う。<br>
     * 引数のImageIconオブジェクトがnullの場合には
     * IllegalArgumentExceptionをスローする。<br>
     *
     * @param imageIcon テクスチャとなるイメージを格納した
     *                  ImageIconオブジェクト
     * @exception  IllegalArgumentException  引数がnullの場合
     */
    public void setTextureFromImageIcon(ImageIcon imageIcon) {
        setBG(imageIcon, true);
        image = null;
        cachedImage = null;
        repaint();
    }

    /**
     * 引数のImgeIconをもとにテクスチャを作成して背景とする。<br>
     * パネルサイズに拡大縮小して背景とするImageオブジェクトが
     * 設定されている場合には、このメソッドを呼び出した際に
     * 破棄されるので、その参照が必要な場合にはgetImageメソッドで
     * 取得してからこのメソッドを呼び出す。<br>
     * このメソッドはrepaintメソッドによりパネル全体の再描画を行う。<br>
     * 引数のImageIconオブジェクトがnullの場合には
     * IllegalArgumentExceptionをスローする。<br>
     *
     * @param imageIcon テクスチャとなるイメージを格納した
     *                  ImageIconオブジェクト
     * @exception  IllegalArgumentException  引数がnullの場合
     */
    public void setTextureFromImage(Image img) {
        if (img == null) {
            throw new IllegalArgumentException("argument is null !");
        }
        setBG(img, true);
        image = null;
        cachedImage = null;
        repaint();
    }

    /**
     * 設定されているテクスチャを返す。<br>
     * 設定されていなければnullを返す。<br>
     *
     * @return 現在設定されているTexturePaintオブジェクトを返す。
     *         設定されていなければnullを返す。
     */
    public TexturePaint getTexture() {
        return texture;
    }

    /**
     * 設定されているイメージやテクスチャがあれば破棄する。<br>
     * このメソッドを呼び出し後はgetImageメソッドとgetTextureメソッドは
     * 共にnullを返すようになる。<br>
     * このパネルの初期状態は透明なのでsetOpaque(true)を呼び出したか
     * どうかにより、背景の無い透明なパネル、あるいは背景の無い不透明な
     * パネルとなる。<br>
     * このメソッドはrepaintメソッドによりパネル全体の再描画を行う。
     */
    public void removeBackgroundImage() {
        image = null;
        texture = null;
        cachedImage = null;
        repaint();
    }

    /**
     * オーバーライド。<br>
     * このクラスではイメージとテクスチャの両方が設定されている状態は
     * 作らないように実装されているが、サブクラスでこれを破る場合には
     * このpaintComponentメソッドをオーバーライドする必要がある。
     *
     * @param g グラフィクコンテキスト
     */
    @Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tempW = rect.width;
        int tempH = rect.height;
        rect.width = getWidth();
        rect.height = getHeight();

        // キャッシュイメージが有効ならばそれを使う。
        // 少なくともSunのJava実装では、あるイメージをあるスケールで
        // 表示した場合には次も同じイメージ、同じスケールならば
        // キャッシュされたイメージがあり、それが表示されるとの事。
        // このクラスではテクスチャをイメージのサイズに敷き詰める
        // 処理が軽減される事を意図してキャッシュイメージを作成しているが、
        // もしかするとこれも意味の無い処理で逆にメモリの無駄遣いとなっている
        // 可能性もある
        if ((cachedImage != null) && (tempW == rect.width) &&
                                     (tempH == rect.height)) {
            g.drawImage(cachedImage, 0, 0, this);
            return;
        }

        //////// 以下キャッシュイメージが無効な場合の処理 ////////

        // 背景となるイメージを作成し、drawImageでグラフィクコンテキストに
        // 描画する。作成したイメージはキャッシュしておき、次回の描画時にも
        // 有効ならば使う。
        // 拡大縮小で描画する場合にイメージが設定されていない場合や
        // テクスチャで描画する場合にTexturePaintオブジェクトが
        // 設定されていない場合には何も描画しない(ただの透明パネル)。
        // イメージとテクスチャの両方が設定されている状態は作らない。
        if (image != null) {
            cachedImage = createImage(rect.width, rect.height);
            Graphics cachedG = cachedImage.getGraphics();
            cachedG.drawImage(image,0, 0,
                              rect.width, rect.height, this);
            cachedG.dispose();
            g.drawImage(cachedImage, 0, 0, this);
        } else if (texture != null) {
            cachedImage = createImage(rect.width, rect.height);
            Graphics2D cachedG = (Graphics2D)cachedImage.getGraphics();
            cachedG.setPaint(texture);
            cachedG.fill(rect);
            cachedG.dispose();
            g.drawImage(cachedImage, 0, 0, this);
        }
    }

    /**
     * 引数のisTextureがtrueの場合は引数のImageIconオブジェクトの保持する
     * イメージからテクスチャを生成し、フィールドtextureに設定する。
     * （この場合にフィールドimageをnullにはしない）<br>
     * 引数のisTextureがfalseの場合は引数のImageIconオブジェクトの保持する
     * イメージをフィールドimageに設定する。（この場合に
     * フィールドtextureをnullにはしない）<br>
     *
     * @param imageIcon イメージを格納したImageIconオブジェクト
     * @param isTexture 引数のイメージをテクスチャとしてパネル全体に
     *                   並べて背景とする場合にはtrueを指定し、
     *                   引数のイメージを拡大縮小させてパネル全体に
     *                   表示する場合にはfalseを指定する。
     *
     * @exception  IllegalArgumentException  引数のImageIconオブジェクトが
     *                                       nullの場合
     */
    protected final void setBG(ImageIcon imageIcon, boolean isTexture) {
        if (imageIcon == null) {
            throw new IllegalArgumentException(
                                   "argument (imageIcon) is null !");
        }
        if (isTexture) {
            //imageIconが正しいイメージを内包していない場合はnullになる
            texture = createTexturePaint(imageIcon);
        } else {
            this.image = imageIcon.getImage();

            //存在しないイメージファイルを引数に作成した
            //ImageIconオブジェクトの場合はpaintComponentで余計な処理を
            //しないようにimageにnullを設定する。
            if ((this.image != null) &&
                ((imageIcon.getIconWidth() <= 0) ||
                                   (imageIcon.getIconHeight() <= 0))) {
                this.image = null;
            }
        }
    }

    /**
     * 引数のisTextureがtrueの場合は引数のイメージから
     * テクスチャを生成し、それをフィールドtextureに設定する。
     * （この場合にフィールドimageをnullにはしない）<br>
     *
     * 引数のisTextureがfalseの場合は引数のイメージをimageに
     * 設定する。（この場合にフィールドtextureをnullにはしない）
     *
     * @param img イメージを格納したImageオブジェクト
     * @param isTexture 引数のイメージをテクスチャとしてパネル全体に
     *                   並べて背景とする場合にはtrueを指定し、
     *                   引数のイメージを拡大縮小させてパネル全体に
     *                   表示する場合にはfalseを指定する。
     * @exception IllegalArgumentException  引数のイメージがnullの場合
     */
    protected final void setBG(Image img, boolean isTexture) {
        if (img == null) {
            throw new IllegalArgumentException(
                                   "argument (img) is null !");
        }
        int imageWidth = img.getWidth(this);
        int imageHeight = img.getHeight(this);

        // イメージがまだ完全にロードされていない場合
        if ((imageWidth < 0) || (imageHeight < 0)) {
            setBG(new ImageIcon(img), isTexture);
            return;
        }

        ////////// 以下imageWidth, imageHeightともに0以上の場合 //////////
        if (isTexture) {
            if ((imageWidth <= 0) || (imageHeight <= 0)) {
                texture = null;
            } else {
                texture = createTexturePaint(img, imageWidth, imageHeight);
            }
        } else {
            if ((imageWidth <= 0) || (imageHeight <= 0)) {
                this.image = null;
            } else {
                this.image = img;
            }
        }
    }

    /**
     * ImageIconオブジェクトからTexturePaintオブジェクトを生成して返す。<br>
     * 引数のImageIconオブジェクトに有効なイメージが格納されていない場合は
     * nullを返す。
     *
     * @param  imageIcon  イメージを格納したImageIconオブジェクト
     * @return 引数のImageIconを基に作られたTexturePaintオブジェクトを返す。
     *         ImageIconが有効なイメージを保持していなかった場合は
     *         nullを返す。
     */
    protected final TexturePaint createTexturePaint(ImageIcon imageIcon) {
        if (imageIcon == null) {
            throw new IllegalArgumentException(
                                   "argument (imageIcon) is null !");
        }

        //イメージの取得。描画できないイメージならnullを返す。
        Image img = imageIcon.getImage();
        int imageWidth = imageIcon.getIconWidth();
        int imageHeight = imageIcon.getIconHeight();
        if ((img == null) || (imageIcon.getIconWidth() <= 0) ||
                             (imageIcon.getIconHeight() <= 0)) {
            return null;
        }
        return createTexturePaint(img, imageWidth, imageHeight);
    }

    /**
     * 引数のイメージから引数で指定した幅、高さのテクスチャを描画する
     * TexturePaintオブジェクトを作成し、フィールドtextureに設定する。<br>
     * このメソッドの引数のImageはロード済みでなければならない。<br>
     *
     * @param img テクスチャとなるImageオブジェクト
     * @param imageWidth  テクスチャの幅
     * @param imageHeight テクスチャの高さ
     * @return 引数の幅、高さにスケーリングされたイメージのテクスチャを
     *         描画する為のTexturePaintオブジェクト
     * @exception IllegalArgumentException 引数のイメージがnullの場合
     * @exception IllegalArgumentException 引数の幅、高さが0以下の場合
     */
    protected final TexturePaint createTexturePaint(Image img,
                                                    int imageWidth,
                                                    int imageHeight) {
        if (img == null) {
            throw new IllegalArgumentException("argument (img) is null !");
        }
        if ((imageWidth <= 0) || (imageHeight <= 0)) {
            throw new IllegalArgumentException(
                    "(imageWidth <= 0) || (imageHeight <= 0)");
        }

        // テクスチャとなるイメージ
        BufferedImage textureImg = null;

        if (img instanceof BufferedImage) {
            textureImg = (BufferedImage)img;
        } else {
            textureImg = new BufferedImage(imageWidth, imageHeight,
                                           BufferedImage.TYPE_INT_ARGB);
            Graphics2D textureG = textureImg.createGraphics();

            textureG.drawImage(img, 0, 0, imageWidth, imageHeight, this);
            textureG.dispose();
        }
        Rectangle rect = new Rectangle(0, 0, imageWidth, imageHeight);

        return new TexturePaint(textureImg, rect);
    }




    ////////// これ以降は直列化と復元に必要なメソッドの実装 //////////

    ///// 直列化の際にのみ使用する定数 /////
    private static final int RECTANGLE_INT    = 1;
    private static final int RECTANGLE_FLOAT  = 2;
    private static final int RECTANGLE_DOUBLE = 3;

    /*
     * 直列化されたデータの復元の際に呼び出されるメソッド。
     * デフォルトの復元の後にwriteObjectで書き出した分のデータを
     * 読み込みフィールドの値を復元する。
     */
    private void readObject(ObjectInputStream in) throws IOException,
                                                  ClassNotFoundException {
        in.defaultReadObject();

        //imageの復元
        int width = in.readInt();
        int height = in.readInt();
        int[] pixels = (int[])in.readObject();

        if (pixels != null) {
            ColorModel colorModel = ColorModel.getRGBdefault();
            MemoryImageSource source = new MemoryImageSource(
                                           width, height, colorModel,
                                           pixels, 0, width);
            image = Toolkit.getDefaultToolkit().createImage(source);
        }

        //texturePaintの復元
        int tpImageWidth = in.readInt();
        int tpImageHeight = in.readInt();

        int rectType = in.readInt();
        Rectangle2D rectangle;

        if (rectType == RECTANGLE_INT) {
            rectangle = new Rectangle(in.readInt(), in.readInt(),
                                      in.readInt(), in.readInt());
        } else if (rectType == RECTANGLE_FLOAT) {
            rectangle = new Rectangle2D.Float(
                                    in.readFloat(), in.readFloat(),
                                    in.readFloat(), in.readFloat());
        } else {
            rectangle = new Rectangle2D.Double(
                                    in.readDouble(), in.readDouble(),
                                    in.readDouble(), in.readDouble());
        }

        int[] tpPixels = (int[])in.readObject();

        if ((tpPixels != null) && (tpPixels.length > 0)) {
            BufferedImage bufferedImage = new BufferedImage(
                                             tpImageWidth,
                                             tpImageHeight,
                                             BufferedImage.TYPE_INT_ARGB);
            bufferedImage.setRGB(0, 0, tpImageWidth, tpImageHeight,
                                 tpPixels, 0, tpImageWidth);
            texture = new TexturePaint(bufferedImage, rectangle);
        }
    }

    /*
     * 直列化の際に呼び出されるメソッド。
     * デフォルトの直列化を行った後にtransientのimage textureの２つの
     * フィールドの復元に必要な情報を書き込む。
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        //imageの直列化
        int imageWidth    = -1;
        int imageHeight   = -1;
        int[] imagePixels = null;

        if (image != null) {
            imageWidth = image.getWidth(this);
            imageHeight = image.getHeight(this);
            if ((imageWidth > 0) && (imageHeight > 0)) {
                imagePixels = getImagePixels(imageWidth,
                                             imageHeight, image);
            }
        }
        out.writeInt(imageWidth);
        out.writeInt(imageHeight);
        out.writeObject(imagePixels);

        //textureの直列化
        int tpImageWidth = -1;
        int tpImageHeight = -1;
        int rectType = RECTANGLE_INT;
        int[] tpPixels = null;
        Rectangle2D rectangle = null;

        if (texture != null) {
            rectangle = texture.getAnchorRect();

            rectType = (rectangle instanceof Rectangle) ?
                       RECTANGLE_INT :
                       (rectangle instanceof Rectangle2D.Float) ?
                       RECTANGLE_FLOAT : RECTANGLE_DOUBLE;


            BufferedImage tpImage = texture.getImage();
            if (tpImage != null) {
                tpImageWidth = tpImage.getWidth();
                tpImageHeight = tpImage.getHeight();
                if ((tpImageWidth > 0) && (tpImageHeight > 0)) {
                    tpPixels = tpImage.getRGB(0, 0, tpImageWidth,
                                              tpImageHeight, null,
                                              0, tpImageWidth);
                }
            }
        }

        out.writeInt(tpImageWidth);
        out.writeInt(tpImageHeight);
        out.writeInt(rectType);

        if (rectangle == null) {
            rectangle = new Rectangle();
        }

        if (rectType == RECTANGLE_INT) {
            Rectangle rect = (Rectangle)rectangle;
            out.writeInt(rect.x);
            out.writeInt(rect.y);
            out.writeInt(rect.width);
            out.writeInt(rect.height);
        } else if (rectType == RECTANGLE_FLOAT) {
            Rectangle2D.Float rect = (Rectangle2D.Float)rectangle;
            out.writeFloat(rect.x);
            out.writeFloat(rect.y);
            out.writeFloat(rect.width);
            out.writeFloat(rect.height);
        } else {
            Rectangle2D.Double rect = (Rectangle2D.Double)rectangle;
            out.writeDouble(rect.x);
            out.writeDouble(rect.y);
            out.writeDouble(rect.width);
            out.writeDouble(rect.height);
        }
        out.writeObject(tpPixels);
    }

    /**
     * イメージからデフォルトのカラーモデルでピクセルデータを取り出す。
     */
    private int[] getImagePixels(int width, int height, Image image) {
        int[] pixels = new int[width * height];
        try {
            PixelGrabber pg = new PixelGrabber(image, 0, 0,
                                               width, height,
                                               pixels, 0, width);
            pg.grabPixels();
            if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                pixels = null;
            }
        }
        catch (InterruptedException e) {
            pixels = null;
        }
        return pixels;
    }
}