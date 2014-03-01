//
//  ClassicLineCell.m
//  OurFarm
//
//  Created by tian hao on 13-6-24.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "ClassicLineCell.h"
#import "ClassicLine.h"
#import "UIImageView+AFNetworking.h"
#import "AMRatingControl.h"

@implementation ClassicLineCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.textLabel.adjustsFontSizeToFitWidth = YES;
        self.textLabel.textColor = [UIColor darkGrayColor];
        self.detailTextLabel.font = [UIFont systemFontOfSize:12.0f];
        self.detailTextLabel.numberOfLines = 0;
        self.selectionStyle = UITableViewCellSelectionStyleGray;
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setClassicLine:(ClassicLine *)classicLine {
    _classicLine = classicLine;
    
    self.name.text = _classicLine.name;
    self.priceInfo.text = _classicLine.priceInfo;
    self.characteristic.text = [NSString stringWithFormat:@"特色:%@", _classicLine.characteristic];
    
    AMRatingControl *simpleRatingControl = [[AMRatingControl alloc] initWithLocation:CGPointMake(0, 0)
                                                                          emptyColor:[UIColor grayColor]
                                                                          solidColor:[UIColor greenColor]
                                                                        andMaxRating:5];
    //set star size
    simpleRatingControl.kFontSize = 13;
    //can not change value
    simpleRatingControl.isSelect = FALSE;
    
    // Customize the current rating
    [simpleRatingControl setRating:[_classicLine.score intValue]];
    [self.starsView addSubview:simpleRatingControl];
    
    __weak UITableViewCell *_weakcell = self;
    
    [self.headImage setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:_classicLine.pic]]
                          placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                   success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                       self.headImage.image = image;
                                       [_weakcell setNeedsLayout];
                                   }
                                   failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                       NSLog(@"Get picture fail in Summary: %@", error);
                                   }];
    
    
    [self setNeedsLayout];
}


@end
