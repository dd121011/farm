//
//  lineCell.h
//  OurFarm
//
//  Created by tian hao on 13-6-26.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Line.h"

@interface LineCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UILabel *name;
@property (strong, nonatomic) IBOutlet UILabel *content;
@property (strong, nonatomic) IBOutlet UILabel *characteristic;

@property (strong, nonatomic) IBOutlet UIImageView *pic1;
@property (strong, nonatomic) IBOutlet UIImageView *pic2;
@property (strong, nonatomic) IBOutlet UIImageView *pic3;
@property (strong, nonatomic) IBOutlet UIImageView *pic4;

@property (strong, nonatomic) Line *line;

@end
