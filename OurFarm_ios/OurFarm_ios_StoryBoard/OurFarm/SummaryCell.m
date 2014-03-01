//
//  SummaryCell.m
//  OurFarm
//
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "SummaryCell.h"
#import "Summary.h"

#import "UIImageView+AFNetworking.h"

#import "AMRatingControl.h"


@implementation SummaryCell{
//@private
//    __strong Summary *_summary;
}

//@synthesize post = _summary;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        self.textLabel.adjustsFontSizeToFitWidth = YES;
        self.textLabel.textColor = [UIColor darkGrayColor];
        self.detailTextLabel.font = [UIFont systemFontOfSize:12.0f];
        self.detailTextLabel.numberOfLines = 0;
        self.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    return self;
}

- (void) setSummaryBase:(Summary *)summary {
    _summary = summary;
    
    self.name.text = _summary.name;
    self.priceInfo.text = _summary.priceInfo;
    self.characteristic.text = [NSString stringWithFormat:@"特色:%@", _summary.characteristic];
    
    AMRatingControl *simpleRatingControl = [[AMRatingControl alloc] initWithLocation:CGPointMake(0, 0)
                                                                          emptyColor:[UIColor grayColor]
                                                                          solidColor:[UIColor greenColor]
                                                                        andMaxRating:5];
    //set star size
    simpleRatingControl.kFontSize = 13;
    //can not change value
    simpleRatingControl.isSelect = FALSE;
    
    // Customize the current rating
    [simpleRatingControl setRating:[_summary.score intValue]];
    [self.starsView addSubview:simpleRatingControl];
    
    __weak UITableViewCell *_weakcell = self;
    
    [self.headimage setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:_summary.pic]]
                          placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                   success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                       self.headimage.image = image;
                                       [_weakcell setNeedsLayout];
                                   }
                                   failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                       NSLog(@"Get picture fail in Summary: %@", error);
                                   }];
    
    
    [self setNeedsLayout];
}
- (void) setSummaryForMyFavorite:(Summary *)summary {
    [self setSummaryBase:summary];
}

- (void)setSummary:(Summary *)summary {
    [self setSummaryBase:summary];
    self.distance.text = [NSString stringWithFormat:@"%@km", summary.distance];
}

+ (CGFloat)heightForCellWithPost:(Summary *) summary {
    CGSize sizeToFit = [summary.address sizeWithFont:[UIFont systemFontOfSize:12.0f] constrainedToSize:CGSizeMake(220.0f, CGFLOAT_MAX) lineBreakMode:UILineBreakModeWordWrap];
    
    return fmaxf(70.0f, sizeToFit.height + 45.0f);
}

#pragma mark - UIView

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.imageView.frame = CGRectMake(10.0f, 10.0f, 50.0f, 50.0f);
    self.textLabel.frame = CGRectMake(70.0f, 10.0f, 240.0f, 20.0f);
    
    CGRect detailTextLabelFrame = CGRectOffset(self.textLabel.frame, 0.0f, 25.0f);
    detailTextLabelFrame.size.height = [[self class] heightForCellWithPost:_summary] - 45.0f;
    self.detailTextLabel.frame = detailTextLabelFrame;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if([segue.identifier isEqualToString:@"desView"]){
        //传递参数
        NSString *type = @"test";
    }
}

@end
