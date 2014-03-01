//
//  ClassicLineCell.h
//  OurFarm
//
//  Created by tian hao on 13-6-24.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
@class ClassicLine;

@interface ClassicLineCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *headImage;
@property (strong, nonatomic) IBOutlet UILabel *name;
@property (strong, nonatomic) IBOutlet UILabel *priceInfo;
@property (strong, nonatomic) IBOutlet UILabel *characteristic;
@property (strong, nonatomic) IBOutlet UIView *starsView;

@property (nonatomic, strong) ClassicLine *classicLine;

@end
