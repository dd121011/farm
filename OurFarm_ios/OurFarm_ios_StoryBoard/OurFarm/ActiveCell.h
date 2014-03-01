//
//  ActiveCell.h
//  OurFarm
//
//  Created by tian hao on 13-6-14.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

#import "Activity.h"

@interface ActiveCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UILabel *name;
@property (strong, nonatomic) IBOutlet UILabel *startTime;
@property (strong, nonatomic) IBOutlet UILabel *endTime;
@property (strong, nonatomic) IBOutlet UITextView *textView;
@property (strong, nonatomic) IBOutlet UIButton *tel;
@property (strong, nonatomic) IBOutlet UIButton *gotoWWW;

@property NSNumber *lat;//纬度
@property NSNumber *lng;//经度

@property (strong, nonatomic) IBOutlet UIButton *callPhone;
@property (strong, nonatomic) IBOutlet UIButton *showAddressOnMap;

@property (nonatomic, strong) Activity *activity;

- (void) showLineOnMap;

@end
