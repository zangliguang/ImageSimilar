Hamming distance

第一步，缩小尺寸。将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。

第二步，简化色彩。将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。

第三步，计算平均值。计算所有64个像素的灰度平均值。

第四步，比较像素的灰度。将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；
小于平均值，记为0。

第五步，计算哈希值。将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。

第六部，计算Hamming distance。如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。

![github](https://raw.githubusercontent.com/zangliguang/ImageSimilar/master/screenshort/S60421-151234_meitu_1.jpg "github")；
![github](https://raw.githubusercontent.com/zangliguang/ImageSimilar/master/screenshort/S60421-151321_meitu_2.jpg "github")；

