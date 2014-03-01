//
//  ActiveCell.m
//  OurFarm
//
//  Created by tian hao on 13-6-14.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "ActiveCell.h"
#import "FarmHomeDB.h"



@implementation ActiveCell{

}

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


- (void) setActivity:(Activity *)activity{
    self.name.text = activity.name;
    self.startTime.text = activity.startTime;
    self.endTime.text = activity.endTime;
    self.textView.text = activity.introduction;
    self.textView.editable = NO;
    
    self.lat = activity.lat;
    self.lng = activity.lng;
    
    [self setNeedsLayout];
}

/**
 * 打开苹果自身地图应用或者使用google地图显示路线
 */
- (void)showLineOnMap
{
    //TODO test line 当前位置如何确认？
    CLLocationCoordinate2D coords1 = CLLocationCoordinate2DMake(30.691793,104.088264);
    CLLocationCoordinate2D coords2 = CLLocationCoordinate2DMake(30.691293,104.088264);
    if (SYSTEM_VERSION_LESS_THAN(@"6.0"))// ios6以下，调用google map
    {
        NSString *urlString = [[NSString alloc]
                               initWithFormat:@"http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f&dirfl=d",
                               coords1.latitude,coords1.longitude,coords2.latitude,coords2.longitude];
        NSURL *aURL = [NSURL URLWithString:urlString];
        //打开网页google地图
        [[UIApplication sharedApplication] openURL:aURL];
    }else// 直接调用ios自己带的apple map
    {    /*
          //keys
          MKLaunchOptionsMapCenterKey:地图中心的坐标(NSValue)
          MKLaunchOptionsMapSpanKey:地图显示的范围(NSValue)
          MKLaunchOptionsShowsTrafficKey:是否显示交通信息(boolean NSNumber)
          
          //MKLaunchOptionsDirectionsModeKey: 导航类型(NSString)
          {
          MKLaunchOptionsDirectionsModeDriving:驾车
          MKLaunchOptionsDirectionsModeWalking:步行
          }
          
          //MKLaunchOptionsMapTypeKey:地图类型(NSNumber)
          enum {
          MKMapTypeStandard = 0,
          MKMapTypeSatellite,
          MKMapTypeHybrid
          };
          */
        
        NSLog(@"lat: %@", self.lat);
        NSLog(@"lng: %@", self.lng);
        
        //打开苹果自身地图应用，并呈现特定的item
        CLLocationCoordinate2D destinationLocation;
        //要去的目标经纬度
        destinationLocation.latitude = [self.lat doubleValue];
        destinationLocation.longitude = [self.lng doubleValue];
        MKMapItem *currentLocation = [MKMapItem mapItemForCurrentLocation];//调用自带地图（定位）
        //显示目的地坐标。画路线
        MKMapItem *toLocation = [[MKMapItem alloc] initWithPlacemark:[[MKPlacemark alloc] initWithCoordinate:destinationLocation addressDictionary:nil]];
        toLocation.name = self.name.text;
        [MKMapItem openMapsWithItems:[NSArray arrayWithObjects:currentLocation, toLocation, nil]
                       launchOptions:[NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:MKLaunchOptionsDirectionsModeDriving, [NSNumber numberWithBool:YES], nil]
                                      
                                                                 forKeys:[NSArray arrayWithObjects:MKLaunchOptionsDirectionsModeKey, MKLaunchOptionsShowsTrafficKey, nil]]];
    }
    
}


- (IBAction)callPhone:(id)sender{
    NSLog(@"Dismiss action sheet with \"phone\"");
    //[self showActionSheet:sender forEvent:nil];
    
    [[[UIAlertView alloc] initWithTitle:@"拨打电话" message:[[self.tel titleLabel] text] delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil] show];
}

- (IBAction)showAddressOnMap:(id)sender {
    NSLog(@"Dismiss action sheet with \"map\"");
    [self showLineOnMap];
}

@end
