//
//  SummaryCell.h
//  OurFarm
//
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
@class Summary;

@interface SummaryCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIImageView *headimage;
@property (strong, nonatomic) IBOutlet UILabel *name;
@property (strong, nonatomic) IBOutlet UILabel *priceInfo;
@property (strong, nonatomic) IBOutlet UILabel *characteristic;
@property (strong, nonatomic) IBOutlet UILabel *distance;
@property (strong, nonatomic) IBOutlet UIView *starsView;

@property (nonatomic, strong) Summary *summary;

- (void) setSummaryBase:(Summary *)summary;
- (void) setSummaryForMyFavorite:(Summary *)summary;

+ (CGFloat)heightForCellWithPost:(Summary *)summary;

@end
