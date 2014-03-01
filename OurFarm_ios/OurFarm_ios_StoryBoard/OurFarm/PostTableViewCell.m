// TweetTableViewCell.m
//
// Copyright (c) 2012 Mattt Thompson (http://mattt.me/)
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

#import "PostTableViewCell.h"

#import "Summary.h"

#import "UIImageView+AFNetworking.h"

@implementation PostTableViewCell {
@private
    __strong Summary *_post;
}

@synthesize post = _post;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (!self) {
        return nil;
    }
    
    self.textLabel.adjustsFontSizeToFitWidth = YES;
    self.textLabel.textColor = [UIColor darkGrayColor];
    self.detailTextLabel.font = [UIFont systemFontOfSize:12.0f];
    self.detailTextLabel.numberOfLines = 0;
    self.selectionStyle = UITableViewCellSelectionStyleGray;
    
    return self;
}

- (void)setPost:(Summary *)post {
    _post = post;

    //setImageWithURL:_post.pic
    const char *pp = [_post.name UTF8String];
    self.textLabel.text = [NSString stringWithUTF8String:pp];
    self.detailTextLabel.text = _post.address;
    
    
    __weak UITableViewCell *_weakcell = self;
    
    [self.imageView setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:_post.pic]]
                          placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                   success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                       _weakcell.imageView.image = image;
                                       
                                       //only required if no placeholder is set to force the imageview on the cell to be laid out to house the new image.
                                       //if(cell.imageView.frame.size.height==0 || cell.imageView.frame.size.width==0 ){
                                       [_weakcell setNeedsLayout];
                                       //}
                                   }
                                   failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                       
                                   }];
    
    //[self.imageView setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:_post.pic]]
    //                placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]];
    
    [self setNeedsLayout];
}

+ (CGFloat)heightForCellWithPost:(Summary *)post {
    CGSize sizeToFit = [post.address sizeWithFont:[UIFont systemFontOfSize:12.0f] constrainedToSize:CGSizeMake(220.0f, CGFLOAT_MAX) lineBreakMode:UILineBreakModeWordWrap];
    
    return fmaxf(70.0f, sizeToFit.height + 45.0f);
}

#pragma mark - UIView

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.imageView.frame = CGRectMake(10.0f, 10.0f, 50.0f, 50.0f);
    self.textLabel.frame = CGRectMake(70.0f, 10.0f, 240.0f, 20.0f);
    
    CGRect detailTextLabelFrame = CGRectOffset(self.textLabel.frame, 0.0f, 25.0f);
    detailTextLabelFrame.size.height = [[self class] heightForCellWithPost:_post] - 45.0f;
    self.detailTextLabel.frame = detailTextLabelFrame;
}

@end
