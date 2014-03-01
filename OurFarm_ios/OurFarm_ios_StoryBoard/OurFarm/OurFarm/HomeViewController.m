//
//  HomeViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-3.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "HomeViewController.h"

@interface HomeViewController ()

@end

@implementation HomeViewController

- (NSInteger)collectionView:(UICollectionView *)view numberOfItemsInSection:(NSInteger)section;
{
    return 4;
}

//- (UICollectionViewCell *)collectionView:(UICollectionView *)cv cellForItemAtIndexPath:(NSIndexPath *)indexPath;
//{
//    // we're going to use a custom UICollectionViewCell, which will hold an image and its label
//    //
//    Cell *cell = [cv dequeueReusableCellWithReuseIdentifier:kCellID forIndexPath:indexPath];
//    
//    // make the cell's title the actual NSIndexPath value
//    cell.label.text = [NSString stringWithFormat:@"{%ld,%ld}", (long)indexPath.row, (long)indexPath.section];
//    
//    // load the image for this cell
//    NSString *imageToLoad = [NSString stringWithFormat:@"%d.JPG", indexPath.row];
//    cell.image.image = [UIImage imageNamed:imageToLoad];
//    
//    return cell;
//}
//
//// the user tapped a collection item, load and set the image on the detail view controller
////
//- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
//{
//    if ([[segue identifier] isEqualToString:@"showDetail"])
//    {
//        NSIndexPath *selectedIndexPath = [[self.collectionView indexPathsForSelectedItems] objectAtIndex:0];
//        
//        // load the image, to prevent it from being cached we use 'initWithContentsOfFile'
//        NSString *imageNameToLoad = [NSString stringWithFormat:@"%d_full", selectedIndexPath.row];
//        NSString *pathToImage = [[NSBundle mainBundle] pathForResource:imageNameToLoad ofType:@"JPG"];
//        UIImage *image = [[UIImage alloc] initWithContentsOfFile:pathToImage];
//        
//        DetailViewController *detailViewController = [segue destinationViewController];
//        detailViewController.image = image;
//    }
//}


@end
